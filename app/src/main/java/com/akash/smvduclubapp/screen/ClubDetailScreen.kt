package com.akash.smvduclubapp.screen

import android.R.attr.contentDescription
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Club
import com.akash.smvduclubapp.data.Event
import com.akash.smvduclubapp.data.createOrJoinChatRoom
import com.akash.smvduclubapp.data.fetchClubs
import com.akash.smvduclubapp.data.fetchCommunityPosts
import com.akash.smvduclubapp.data.fetchEvents
import com.akash.smvduclubapp.data.fetchEventsByClubId
import com.akash.smvduclubapp.data.saveUserClubRelationToSupabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDetailScreen(navController: NavController, clubId: String?) {
    val scope = rememberCoroutineScope()
    var allClubs by remember { mutableStateOf<List<Club>>(emptyList()) }
    var isClubLoading by remember { mutableStateOf(true) }

    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isEventLoading by remember { mutableStateOf(true) }

    var showJoinDialog by remember { mutableStateOf(false) }
    var isJoining by remember { mutableStateOf(false) }
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser = remember { firebaseAuth.currentUser }

    // Safely get user ID and name with fallbacks
    val currentUserId = remember { firebaseUser?.uid ?: "" }
    val userName = remember { firebaseUser?.displayName ?: "Anonymous User" }
    // Find current club using clubId instead of name
    val club = allClubs.find { it.clubId == clubId }

    val scrollState = rememberScrollState()
    val isScrolling = remember { derivedStateOf { scrollState.value > 10 } }

    var isClubMember by remember { mutableStateOf(false) }
    var chatRoomId by remember { mutableStateOf("") }

    // Check if user is already a member of this club's chat room
    LaunchedEffect(clubId, currentUserId) {
        if (!clubId.isNullOrEmpty() && currentUserId.isNotEmpty()) {
            try {
                val db = FirebaseFirestore.getInstance()
                val chatRoomsQuery = db.collection("chatrooms")
                    .whereEqualTo("clubId", clubId)
                    .whereArrayContains("memberIds", currentUserId)
                    .get()
                    .await()

                // If user is in the members list of this club's chat room
                if (!chatRoomsQuery.isEmpty) {
                    isClubMember = true
                    chatRoomId = chatRoomsQuery.documents[0].id
                }
            } catch (e: Exception) {
                Log.e("Club", "Error checking membership: ${e.message}")
            }
        }
    }

    // Fetch clubs
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                allClubs = fetchClubs()
            } catch (e: Exception) {
                Log.e("Club", "Error fetching club list: ${e.message}")
            } finally {
                isClubLoading = false
            }
        }
    }

    // Fetch events for the specific club
    LaunchedEffect(clubId) {
        scope.launch {
            try {
                if (!clubId.isNullOrEmpty()) {
                    events = fetchEventsByClubId(clubId)
                }
            } catch (e: Exception) {
                Log.e("Event", "Error fetching events for club $clubId: ${e.message}")
            } finally {
                isEventLoading = false
            }
        }
    }
    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = { Text("Join Club") },
            text = { Text("Do you want to join ${club?.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        showJoinDialog = false
                        scope.launch {
                            isJoining = true
                            try {
                                club?.let {
                                    val chatRoom = createOrJoinChatRoom(it, currentUserId, userName)

                                    // Save relationship to Supabase
                                    saveUserClubRelationToSupabase(currentUserId, it.clubId)
                                    // Set membership state and room ID
                                    isClubMember = true
                                    chatRoomId = chatRoom.roomId
                                    // Navigate to chat room
                                    navController.navigate("chatRoom/${chatRoom.roomId}")
                                }
                            } catch (e: Exception) {
                                Log.e("Club", "Error joining club: ${e.message}")
                            } finally {
                                isJoining = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.theme_color)
                    )
                ) {
                    Text("Yes", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showJoinDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("No")
                }
            }
        )
    }


    // If club is loading
    if (isClubLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // If club not found
    if (club == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(id = R.string.club_error),
                fontSize = 20.sp,
                color = Color.Red
            )
        }
        return
    }

    // Main UI
    Scaffold(
        topBar = {
            Box(modifier = Modifier.height(0.dp)) {
                AnimatedVisibility(visible = !isScrolling.value) {
                    TopAppBar(
                        title = { Text(text = club.name, fontSize = 20.sp) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            val imageSize by animateDpAsState(if (isScrolling.value) 80.dp else 120.dp)
            val textSize by animateFloatAsState(if (isScrolling.value) 20f else 28f)

            // Club Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorResource(id = R.color.theme_color),
                                Color(0xFF005F9E)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = rememberAsyncImagePainter(club.club_logo),
                        contentDescription = "${club.name} Logo",
                        modifier = Modifier
                            .size(imageSize)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = club.name,
                        fontSize = textSize.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            AnimatedVisibility(visible = !isScrolling.value) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = club.description,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedVisibility(visible = !isScrolling.value) {
                Button(
                    onClick = {
                        if (isClubMember) {
                            // Direct navigation to chat if already a member
                            navController.navigate("chatRoom/$chatRoomId")
                        } else {
                            // Show join dialog if not a member
                            showJoinDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.theme_color)
                    ),
                    enabled = !isJoining
                ) {
                    if (isJoining) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = if (isClubMember)
                                "Chat"
                            else
                                stringResource(id = R.string.join_club),
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.events_organized),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Loading events indicator
            if (isEventLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (events.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 54.dp), // Adjusts position downward
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy, // Choose your preferred icon
                            contentDescription = "No Events Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp)) // Space between icon and text
                        Text(
                            text = "No events available for this club",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
        } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                ) {
                    events.forEach { event ->
                        EventCard(event, navController)
                    }
                }
            }
        }
    }
}



// Event Card UI
@Composable
fun EventCard(event: Event,navController: NavController) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp)
            .clickable {
                // Handle Click Action Here
                navController.navigate("eventDetails/${event.name}")
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,

            )
            Spacer(modifier = Modifier.height(6.dp))
            Image(
                painter = rememberAsyncImagePainter(event.event_poster),
                contentDescription = "${event.name} Poster",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(15.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = event.description,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "📅 ${event.event_date}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF007BFF)
            )
        }
    }
}
