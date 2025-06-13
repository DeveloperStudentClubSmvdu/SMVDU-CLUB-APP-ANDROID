package com.akash.smvduclubapp.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Event
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import coil.compose.rememberAsyncImagePainter
import com.akash.smvduclubapp.Screen
import com.akash.smvduclubapp.data.fetchCommunityPosts
import com.akash.smvduclubapp.data.fetchEvents
import com.akash.smvduclubapp.data.fetchUserEvents
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var userEvents by remember { mutableStateOf<List<Event>>(emptyList()) }
    var sortOrder by remember { mutableStateOf(SortOrder.DATE_TIME) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: return

    // Fetch user's events when the Composable is launched
    LaunchedEffect(true) {
        scope.launch {
            try {
                userEvents = fetchUserEvents(userId) // Replace with actual user ID
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching user events: ${e.message}")
                Toast.makeText(
                    context,
                    "Failed to load your events: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                isLoading = false
            }
        }
    }

    val filteredEvents = remember(searchQuery, userEvents, sortOrder) {
        // First filter based on search query
        val filtered = if (searchQuery.isBlank()) userEvents
        else userEvents.filter { event ->
            event.name.contains(searchQuery, ignoreCase = true) ||
                    event.description?.contains(searchQuery, ignoreCase = true) == true
        }

        // Then sort based on the selected sort order
        when (sortOrder) {
            SortOrder.ALPHABETICAL -> filtered.sortedBy { it.name.lowercase() }
            SortOrder.DATE_TIME -> filtered.sortedByDescending { event -> event.event_date }
            else -> filtered
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading your events...", color = Color.Gray)
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(4.dp)
    ) {
        // Search Bar Row with sorting button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                placeholder = {
                    Text(
                        text = "Search your events...",
                        color = MaterialTheme.colorScheme.scrim,
                        fontSize = 16.sp
                    )
                },
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(R.color.theme_color),
                    unfocusedBorderColor = Color.LightGray,
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Sort Button
            IconButton(
                onClick = {
                    sortOrder = if (sortOrder == SortOrder.ALPHABETICAL) {
                        SortOrder.DATE_TIME
                    } else {
                        SortOrder.ALPHABETICAL
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (sortOrder == SortOrder.ALPHABETICAL) {
                        Icons.Default.SortByAlpha
                    } else {
                        Icons.Default.DateRange
                    },
                    contentDescription = "Sort Events",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (filteredEvents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "You haven't registered for any events yet",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate(Screen.EventListScreen.route) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.theme_color)
                        )
                    ) {
                        Text("Explore Events")
                    }
                }
            }
        } else {
            // Event List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEvents) { event ->
                    EventCard(event = event, onClick = {
                        navController.navigate("eventDetails/${event.name}")
                    })
                }
            }
        }
    }
}