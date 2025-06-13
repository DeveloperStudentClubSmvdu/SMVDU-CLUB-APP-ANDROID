package com.akash.smvduclubapp.screen.mainscreen

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.Screen
import com.akash.smvduclubapp.data.Announcement
import com.akash.smvduclubapp.data.BottomNavItem
import com.akash.smvduclubapp.data.ClubCategory
import com.akash.smvduclubapp.data.CommunityPost
import com.akash.smvduclubapp.data.Event
import com.akash.smvduclubapp.data.Fest
import com.akash.smvduclubapp.data.User
import com.akash.smvduclubapp.data.addCommunityPost
import com.akash.smvduclubapp.data.fetchClubCategories
import com.akash.smvduclubapp.data.fetchCommunityPosts
import com.akash.smvduclubapp.data.fetchFests
import com.akash.smvduclubapp.data.fetchLatestAnnouncement
import com.akash.smvduclubapp.data.fetchUpcomingEvents
import com.akash.smvduclubapp.data.fetchUser
import com.akash.smvduclubapp.data.fetchUserName
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        WelcomeSection()
        Spacer(modifier = Modifier.height(12.dp))
        ViceChancellorMessageSection(navController)
        Spacer(modifier = Modifier.height(12.dp))
        AnnouncementsSection()
        Spacer(modifier = Modifier.height(12.dp))
        UpcomingEventsSection(navController)
        Spacer(modifier = Modifier.height(12.dp))
        ActivityHeadSection(navController)
        Spacer(modifier = Modifier.height(12.dp))
        UniversityFestSection(navController)
        Spacer(modifier = Modifier.height(12.dp))
        CommunityFeedSection()
    }
}
// In mainscreen/HomeTopBar.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    title: String,
    clubLogo: String? = null,
    onNotificationClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                if (clubLogo != null) {
                    // Display club logo for chat screens
                    Image(
                        painter = rememberAsyncImagePainter(clubLogo),
                        contentDescription = "$title Logo",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            //.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
            // Back button for chat and other non-main screens
        actions = {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            }
        },

    )
}
