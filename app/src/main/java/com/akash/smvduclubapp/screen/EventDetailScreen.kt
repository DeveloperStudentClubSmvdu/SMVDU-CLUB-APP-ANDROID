package com.akash.smvduclubapp.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Event
import com.akash.smvduclubapp.data.EventDescription
import com.akash.smvduclubapp.data.TopicData
import com.akash.smvduclubapp.data.fetchEventDescription
import com.akash.smvduclubapp.data.fetchEvents
import com.akash.smvduclubapp.data.isUserRegisteredForEvent
import com.akash.smvduclubapp.data.topicColors
import kotlinx.coroutines.launch


@Composable
fun EventDetailScreen(navController: NavController, eventTitle: String?) {
    val scope = rememberCoroutineScope()
    var allEvents by remember { mutableStateOf<List<Event>>(emptyList()) }

    // Fetch events when the Composable is launched
    var isLoading by remember { mutableStateOf(true) }
    var isUserRegistered by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        scope.launch {
            try {
                allEvents = fetchEvents()
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching events list: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
    val event = allEvents.find { it.name == eventTitle }

    LaunchedEffect(event) {
        event?.id?.let { eventId ->
            isUserRegistered = isUserRegisteredForEvent(eventId)
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    if (event == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.event_error),
                fontSize = 20.sp,
                color = Color.Red
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = stringResource(R.string.event_detail),
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(end = 36.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .offset(y = (-40).dp) // Move it upward to overlay on the card

        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .shadow(10.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.clubcardcolor)),
                shape = RoundedCornerShape(16.dp)
            ) {
                // Center Content inside the Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(25.dp),

                    contentAlignment = Alignment.Center // Ensures everything is centered
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally // Centers text inside Column
                    ) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = event.name,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color= MaterialTheme.colorScheme.scrim,
                            modifier = Modifier.align(Alignment.CenterHorizontally) // Centers the text explicitly
                        )
                        Spacer(modifier = Modifier.height(25.dp))

                        // Event Tagline Button
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(Alignment.CenterHorizontally), // Centers Button inside Column
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text(
                                text = event.event_title,
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top= 0.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp, vertical = 0.dp)
                        .shadow(12.dp, shape = RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.Black)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(event.event_poster),
                        contentDescription = "${event.name} Poster",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .clip(RoundedCornerShape(15.dp))
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                // 🔹 Event Details
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Event Title
                    Text(text = event.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.baseline_calendar_today_24),
                            contentDescription = "Date",
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.event_date,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Time Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.baseline_access_time_24),
                            contentDescription = "Time",
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = event.event_time, fontSize = 16.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Venue Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.baseline_location_pin_24),
                            contentDescription = "Time",
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = event.event_venue, fontSize = 16.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = event.description,
                        fontSize = 16.sp,
                        //color = MaterialTheme.colorScheme.outlineVariant,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // 🔹 Topics Section
                    Text(text = stringResource(R.string.topics), fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Check if description_id exists before showing topics
                    if (event.description_id != null) {
                        TopicList(event.description_id)
                    } else {
                        Text(
                            text = "No topics available for this event",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                    // 🔹 Register Button
                    val isRegistrationClosed = remember {
                        try {
                            // Parse the event registration date
                            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            val registrationDate = dateFormat.parse(event.event_registration_date)
                            val currentDate = java.util.Date()

                            // Check if registration date has passed
                            registrationDate?.before(currentDate) ?: false
                        } catch (e: Exception) {
                            Log.e("EventDetail", "Error parsing date: ${e.message}")
                            false
                        }
                    }

                    Button(
                        onClick = {
                            if (!isUserRegistered) {
                                navController.navigate("eventregistrationform/${event.name}/${event.id}")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isRegistrationClosed -> Color.Gray
                                isUserRegistered -> Color.Green.copy(alpha = 0.7f)
                                else -> Color(0xFF007BFF)
                            },
                            disabledContainerColor = Color.Gray
                        ),
                        enabled = !isRegistrationClosed && !isUserRegistered
                    ) {
                        Text(
                            text = when {
                                isRegistrationClosed -> "Registration Closed"
                                isUserRegistered -> "Registered"
                                else -> "Register Now"
                            },
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopicList(description_id: Long) {
    // State to hold the event description
    var eventDescription by remember { mutableStateOf<EventDescription?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // LaunchedEffect to fetch the event description when the composable enters the composition
    LaunchedEffect(key1 = description_id) {
        isLoading = true
        error = null

        try {
            // Convert description_id to String directly here
            val result = fetchEventDescription(description_id.toString())
            eventDescription = result
        } catch (e: Exception) {
            error = e.message ?: "Unknown error occurred"
            Log.e("TopicList", "Error fetching event description for ID $description_id", e)
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .background(
                color = colorResource(id = R.color.clubcardcolor),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            error != null -> {
                Text(
                    text = "Failed to load topics: $error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            eventDescription != null -> {
                // Extract topic data, filtering out any null topics
                val topicDataList = buildList {
                    eventDescription?.Topic1?.let { name ->
                        eventDescription?.Topic1_description?.let { desc ->
                            add(TopicData(name, desc))
                        }
                    }
                    eventDescription?.Topic2?.let { name ->
                        eventDescription?.Topic2_description?.let { desc ->
                            add(TopicData(name, desc))
                        }
                    }
                    eventDescription?.Topic3?.let { name ->
                        eventDescription?.Topic3_description?.let { desc ->
                            add(TopicData(name, desc))
                        }
                    }
                    eventDescription?.Topic4?.let { name ->
                        eventDescription?.Topic4_description?.let { desc ->
                            add(TopicData(name, desc))
                        }
                    }
                    eventDescription?.Topic5?.let { name ->
                        eventDescription?.Topic5_description?.let { desc ->
                            add(TopicData(name, desc))
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (topicDataList.isEmpty()) {
                        Text(
                            text = "No topics available",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        topicDataList.forEachIndexed { index, topicData ->
                            // Each topic gets its designated color
                            val color = topicColors[index % topicColors.size]
                            TopicItem(topicData.name, topicData.description, color)
                        }
                    }
                }
            }
            else -> {
                Text(
                    text = "No data available",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// Reusable Topic Item
@Composable
fun TopicItem(topicName: String, description: String, color: Color) {
    var isExpanded by remember { mutableStateOf(false) } // Track expansion state

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Topic Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.1f))
                .padding(vertical = 10.dp, horizontal = 16.dp)
                .clickable { isExpanded = !isExpanded }, // Toggle expansion
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = topicName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Expand/Collapse",
                    tint = color
                )
            }
        }

        // Expandable Description
        AnimatedVisibility(visible = isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Text(text = description, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}



























