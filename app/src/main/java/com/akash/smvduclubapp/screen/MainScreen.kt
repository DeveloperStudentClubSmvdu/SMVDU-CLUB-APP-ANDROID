package com.akash.smvduclubapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.Screen
import com.akash.smvduclubapp.data.BottomNavItem
import com.akash.smvduclubapp.data.Club
import com.akash.smvduclubapp.data.getDummyCommunityPosts
import com.akash.smvduclubapp.data.getDummyEvents
import com.akash.smvduclubapp.data.getDummyUniversityFests


@Composable
fun MainScreen(navController: NavHostController, userName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        WelcomeSection(userName = userName)
        Spacer(modifier = Modifier.height(12.dp))
        AnnouncementsSection()
        Spacer(modifier = Modifier.height(12.dp))
        UpcomingEventsSection(navController)
        Spacer(modifier = Modifier.height(12.dp))
        ClubsListSection()
        Spacer(modifier = Modifier.height(12.dp))
        UniversityFestSection(navController)
        Spacer(modifier = Modifier.height(12.dp))
        CommunityFeedSection()
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = BottomNavItem.dummyData() // Fetch bottom nav items

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(painter = screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.MainScreen.route) { inclusive = false }
                            launchSingleTop = true // Prevents reloading the same screen
                        }
                    }
                }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(title: String, onNavigationToNotification: () -> Unit) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(onClick = onNavigationToNotification) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
        }
    )
}

@Composable
fun WelcomeSection(userName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "👋 Welcome, $userName!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = stringResource(id = R.string.welcome_description), // Convert resource ID to String
                fontSize = 16.sp,
                color = Color(0xFF595959) // textColor
            )
        }
    }
}

@Composable
fun AnnouncementsSection() {
    Text(

        text = "📢 Announcements",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2645E8))
    ) {
        Text(
            text = stringResource(id = R.string.announcemets_desc),
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun UpcomingEventsSection(navController : NavHostController) {
    val events = getDummyEvents()

    Text(
        text = "🎟️ Upcoming Events",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )

    LazyRow(modifier = Modifier.padding(8.dp)) {
        items(events) { event ->
            EventCard(eventName = event.title, eventDate = event.date, eventTime = event.time,navController )
        }
    }
}

@Composable
fun EventCard(eventName: String, eventDate: String, eventTime: String,navController : NavHostController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(200.dp)  // Increased width for better spacing
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.cardcolor))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = eventName, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "$eventDate • $eventTime", fontSize = 14.sp, color = Color.Black.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { /* Handle RSVP */
                    val event = eventName
                    navController.navigate("eventDetails/${event}")
                         },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.button2color))
            ) {
                Text("More...", color = Color.White)
            }
        }
    }
}
@Composable
fun UniversityFestSection(navController: NavController) {
    val festList = getDummyUniversityFests()

    Text(
        text = "🎉 University Fest",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )

    LazyRow(modifier = Modifier.padding(8.dp)) {
        items(festList) { fest ->
            FestCard(festName = fest.title, festDate = fest.date, festDescription = fest.description, navController = navController)
        }
    }
}

@Composable
fun FestCard(festName: String, festDate: String, festDescription: String, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(200.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.cardcolor))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = festName, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "$festDate", fontSize = 14.sp, color = Color.Black.copy(alpha = 0.8f))
            Text(text = festDescription, fontSize = 12.sp, color = Color.Black.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { navController.navigate("festDetail/${festName}") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.button2color))
            ) {
                Text("More...", color = Color.White)
            }
        }
    }
}



@Composable
fun ClubsListSection() {
    val clubList = Club.getDummyClubs()  // Fetch club data

    Text(
        text = "🏆 Explore Clubs",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp) // Increased height for better scrolling experience
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(clubList) { club ->
                ClubCard(club = club)
            }
        }
    }
}

@Composable
fun ClubCard(club: Club) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.cardcolor))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = club.logoResId),
                contentDescription = "${club.name} Logo",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = club.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = club.description,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = { /* Handle Club Join */ },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.button2color))
            ) {
                Text("Join", color = Color.White)
            }
        }
    }
}

@Composable
fun CommunityFeedSection() {
    val posts = remember { getDummyCommunityPosts() } // Fetch dummy posts

    Text(
        text = "💬 Community Feed",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        posts.take(3).forEach { post ->  // Display only the first 3 posts
            PostCard(username = post.username, postContent = post.postContent)
        }
    }
}


@Composable
fun PostCard(username: String, postContent: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.cardcolor))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = username, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = postContent, fontSize = 14.sp)
        }
    }
}

