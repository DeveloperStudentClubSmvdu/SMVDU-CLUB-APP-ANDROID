package com.akash.smvduclubapp.screen.mainscreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Announcement
import com.akash.smvduclubapp.data.fetchLatestAnnouncement
import kotlinx.coroutines.launch


@Composable
fun AnnouncementsSection() {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var latestAnnouncement by remember { mutableStateOf<Announcement?>(null) }

// Fetch the latest announcement when the screen is launched
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                latestAnnouncement = fetchLatestAnnouncement()
            } catch (e: Exception) {
                Log.e("Announcements", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Text(
        text = "📢 Announcements",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )

    if (isLoading) {
        // Show shimmer or progress while loading
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp)),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2645E8))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.loading),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp)),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2645E8))
        ) {
            Text(
                text = latestAnnouncement?.message ?: stringResource(R.string.no_announcements_available),
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
