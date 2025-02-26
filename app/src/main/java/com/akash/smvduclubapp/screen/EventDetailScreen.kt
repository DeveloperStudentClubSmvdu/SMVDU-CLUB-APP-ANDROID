package com.akash.smvduclubapp.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.getDummyEvents
import com.akash.smvduclubapp.data.getTopics


@Composable
fun EventDetailScreen(navController: NavController, eventTitle: String?) {
    val event = getDummyEvents().find { it.title == eventTitle }

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
            .background(Color.White)
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
                            text = event.title,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
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
                                text = event.description,
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp, vertical = 15.dp)
                        .shadow(12.dp, shape = RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White)
                ) {
                    Image(
                        painter = painterResource(id = event.posterResId),
                        contentDescription = "${event.title} Poster",
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
                    Text(text = event.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)

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
                            text = event.date,
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
                        Text(text = event.time, fontSize = 16.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = event.description,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // 🔹 Topics Section
                    Text(text = stringResource(R.string.topics), fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))

                    TopicList()

                    Spacer(modifier = Modifier.height(30.dp))
                    // 🔹 Register Button
                    Button(
                        onClick = { /* Handle Registration */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
                    ) {
                        Text(text = "Register Now", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
@Composable
fun TopicList() {
    val topics = remember { getTopics() } // Fetch topics from data file

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        topics.forEach { topic ->
            TopicItem(topic.name, topic.description, topic.color)
        }
    }
}

// 🔹 Reusable Topic Item
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
            contentAlignment = Alignment.Center
        ) {
            Text(text = topicName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
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




























