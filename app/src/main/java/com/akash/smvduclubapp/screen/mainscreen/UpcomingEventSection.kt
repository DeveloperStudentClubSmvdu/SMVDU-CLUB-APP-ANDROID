package com.akash.smvduclubapp.screen.mainscreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Event
import com.akash.smvduclubapp.data.fetchUpcomingEvents
import kotlinx.coroutines.launch

@Composable
fun UpcomingEventsSection(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var upcomingEvents by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        scope.launch {
            try {
                upcomingEvents = fetchUpcomingEvents()
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching upcoming events list: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Text(
        text = "🎟️ Upcoming Events",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    } else if (upcomingEvents.isEmpty()) {
        Text(
            text = stringResource(R.string.no_upcoming_events_found) + stringResource(R.string.go_and_study_for_exams),
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyRow(modifier = Modifier.padding(8.dp)) {
            items(upcomingEvents) { event ->
                EventCard(
                    eventName = event.name,
                    eventDate = event.event_date,
                    eventTime = event.event_time,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun EventCard(eventName: String, eventDate: String, eventTime: String, navController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(200.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = eventName,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = eventDate, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f))
            Text(text = eventTime, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { navController.navigate("eventDetails/${eventName}") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text("More...", color = Color.White)
            }
        }
    }
}