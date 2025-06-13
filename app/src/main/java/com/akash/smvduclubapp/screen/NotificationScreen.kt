package com.akash.smvduclubapp.screen

import android.util.Log

import com.akash.smvduclubapp.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
// ...existing imports...
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.akash.smvduclubapp.data.NotificationData
import com.akash.smvduclubapp.database.NotificationRepository
import com.akash.smvduclubapp.ui.theme.poppinsFontFamily
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Chat
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.akash.smvduclubapp.data.fetchRecentNotifications

@Composable
fun NotificationScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val notifications = remember { mutableStateOf<List<NotificationData>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }

    // Fetch notifications when the screen is launched
    LaunchedEffect(Unit) {
        isLoading.value = true
        try {
            val allNotifications = fetchRecentNotifications() // Now fetches all notifications without filtering
            notifications.value = allNotifications
        } catch (e: Exception) {
            Log.e("NotificationScreen", "Error fetching notifications", e)
        } finally {
            isLoading.value = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with Back Button and Clear Button
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = stringResource(id = R.string.notifications),
                    fontSize = 24.sp,
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center
                )
                TextButton(onClick = { }) {
                    Text(
                        text = "Clear",
                        fontSize = 16.sp,
                        color = Color.White,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading.value -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            notifications.value.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painterResource(R.drawable.baseline_notifications_off_24),
                            contentDescription = "No Notifications",
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = stringResource(id = R.string.no_notifications),
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(notifications.value) { notification ->
                        NotificationCard(notification, navController) {
                            // Here you would implement the mark as read functionality
                            // This would require a function to update the notification status in Supabase
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationData, navController: NavController, markAsRead: () -> Unit) {
    val backgroundColor = if (!notification.isRead) MaterialTheme.colorScheme.secondaryContainer
    else MaterialTheme.colorScheme.primaryContainer
    val titleFontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal
    val isChat = notification.type == "chat"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                markAsRead()
                if (isChat && notification.itemId.isNotEmpty()) {
                    navController.navigate("chatDetails/${notification.itemId}")
                } else if (notification.itemId.isNotEmpty()) {
                    navController.navigate("eventDetails/${notification.itemId}")
                }
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        // Card content remains the same but uses notification.isRead status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFEAEAEA), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isChat) Icons.Default.Chat else Icons.Default.Notifications,
                    contentDescription = if (isChat) "Chat Notification" else "Notification Icon",
                    tint = if (isChat) Color(0xFF4CAF50) else Color(0xFF007BFF),
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = titleFontWeight,
                    fontSize = 16.sp
                )
                if (isChat) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chat",
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier
                            .background(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = notification.date,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = titleFontWeight
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = notification.time,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}