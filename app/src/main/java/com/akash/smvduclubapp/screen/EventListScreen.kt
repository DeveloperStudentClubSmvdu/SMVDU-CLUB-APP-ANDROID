package com.akash.smvduclubapp.screen

import android.util.Log
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import coil.compose.rememberAsyncImagePainter
import com.akash.smvduclubapp.data.fetchCommunityPosts
import com.akash.smvduclubapp.data.fetchEvents
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") } // Search query state
    val scope = rememberCoroutineScope()
    var allEvents by remember { mutableStateOf<List<Event>>(emptyList()) }

    // Add back the sortOrder state to support alphabetical sorting
    var sortOrder by remember { mutableStateOf(SortOrder.DATE_TIME) } // Default to date/time sorting

    // Fetch events when the Composable is launched
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(true) {
        scope.launch {
            try {
                allEvents = fetchEvents()
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching posts list: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    val filteredEvents = remember(searchQuery, allEvents, sortOrder) {
        // First filter based on search query
        val filtered = if (searchQuery.isBlank()) allEvents
        else allEvents.filter { event ->
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
                Text("Loading events...", color = Color.Gray)
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
        // Search Bar Row - Added back sorting button
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
                    .weight(1f)  // Takes most of the width but leaves space for sort button
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.event_search),
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
                    // Toggle between date and alphabetical sorting
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

        // Event List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredEvents) { event -> // Show only filtered events
                EventCard(event = event, onClick = {
                    navController.navigate("eventDetails/${event.name}")
                })
            }
        }
    }
}

// Updated SortOrder enum
enum class SortOrder {
    NONE,
    ALPHABETICAL,
    DATE_TIME
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Event Image on Left Side
            Image(
                painter = rememberAsyncImagePainter(event.event_poster), // Using local drawable resource
                contentDescription = "Event Image",
                modifier = Modifier
                    .size(width = 115.dp, height = 150.dp) // Adjust size
                    .clip(RoundedCornerShape(8.dp)) // Rounded corners
                    .background(Color.LightGray), // Placeholder background
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Event Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_calendar_today_24),
                        contentDescription = "Date",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = event.event_date, fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Time Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_access_time_24),
                        contentDescription = "Time",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = event.event_time, fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Event Description
                Text(
                    text = event.description.take(60) + "...",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}