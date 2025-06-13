package com.akash.smvduclubapp.screen

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
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Event
import com.akash.smvduclubapp.data.Fest
import com.akash.smvduclubapp.data.fetchFests

import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import coil.compose.rememberAsyncImagePainter
import com.akash.smvduclubapp.data.fetchCommunityPosts
import com.akash.smvduclubapp.data.fetchEvents
import com.akash.smvduclubapp.data.fetchEventsByFestId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestDetailScreen(navController: NavController, festName: String?) {
    val scope = rememberCoroutineScope()
    var allFests by remember { mutableStateOf<List<Fest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch all fests when the Composable is launched
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                allFests = fetchFests()
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching fest detail: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // Find the fest dynamically by name
    val fest = allFests.find { it.name == festName }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }

    // Fetch events filtered by fest ID when the fest is found
    LaunchedEffect(fest?.id) {
        if (fest != null) {
            scope.launch {
                try {
                    // Fetch only events associated with this fest by ID
                    events = fetchEventsByFestId(fest.id)
                } catch (e: Exception) {
                    Log.e("Supabase", "Error fetching events for fest ${fest.id}: ${e.message}")
                }
            }
        }
    }

    val scrollState = rememberScrollState()
    val isScrolling = remember { derivedStateOf { scrollState.value > 10 } }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    if (fest == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(id = R.string.fest_error),
                fontSize = 20.sp,
                color = Color.Red
            )
        }
        return
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.height(0.dp)
            ) {
                AnimatedVisibility(visible = !isScrolling.value) {
                    TopAppBar(
                        title = { Text(text = fest.name, fontSize = 20.sp) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                                    contentDescription = "Back"
                                )
                            }
                        },
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
            val imageSize by animateDpAsState(if (isScrolling.value) 70.dp else 120.dp)
            val textSize by animateFloatAsState(if (isScrolling.value) 18f else 28f)
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {
                    Image(
                        painter = rememberAsyncImagePainter(fest.fest_logo),
                        contentDescription = "${fest.name} Logo",
                        modifier = Modifier
                            .size(imageSize)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = fest.name,
                        fontSize = textSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            AnimatedVisibility(visible = !isScrolling.value) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = fest.description,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.events_organized),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .height(650.dp)
            )  {
                if (events.isEmpty()) {
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
                    val eventScrollState = rememberScrollState()
                    val isEventScrollable = remember { derivedStateOf { isScrolling.value } }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(if (isEventScrollable.value) Modifier.verticalScroll(eventScrollState) else Modifier)
                    ) {
                        events.forEach { event ->
                            FestEventCard(event, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FestEventCard(event: Event, navController: NavController) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp)
            .clickable {
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