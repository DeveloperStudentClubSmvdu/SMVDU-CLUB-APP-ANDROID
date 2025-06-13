package com.akash.smvduclubapp.screen

import android.util.Log
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Club
import com.akash.smvduclubapp.data.fetchUserClubs
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyClubScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var userClubs by remember { mutableStateOf<List<Club>>(emptyList()) }
    var isAlphabeticallySorted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // Get current user ID from authentication
                val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

                if (userId != null) {
                    userClubs = fetchUserClubs(userId)
                } else {
                    error = "User not logged in"
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching user clubs: ${e.message}")
                error = "Failed to load clubs"
            } finally {
                isLoading = false
            }
        }
    }

    val filteredClubs = remember(searchQuery, userClubs, isAlphabeticallySorted) {
        // First filter based on search query
        val filtered = if (searchQuery.isBlank()) userClubs
        else userClubs.filter { club ->
            club.name.contains(searchQuery, ignoreCase = true) ||
                    club.description.contains(searchQuery, ignoreCase = true)
        }

        // Then apply sorting if needed
        if (isAlphabeticallySorted) {
            filtered.sortedBy { it.name }
        } else {
            filtered
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(4.dp)
    ) {
        // Search Bar and Sort Button Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
                        RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                placeholder = {
                    Text(
                        text = "Search your clubs...",
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
                        tint = MaterialTheme.colorScheme.scrim,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(R.color.theme_color),
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Sort Alphabetically Button
            IconButton(
                onClick = { isAlphabeticallySorted = !isAlphabeticallySorted },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isAlphabeticallySorted)
                            colorResource(R.color.theme_color)
                        else
                            MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.SortByAlpha,
                    contentDescription = "Sort Alphabetically",
                    tint = if (isAlphabeticallySorted) Color.White else Color.Black
                )
            }
        }

        when {
            isLoading -> {
                // Show loading indicator while fetching data
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            error != null -> {
                // Show error message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            userClubs.isEmpty() -> {
                // Show message when user hasn't joined any clubs
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "You haven't joined any clubs yet",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = "Go to the Clubs tab to discover and join clubs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                // Clubs List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredClubs) { club ->
                        ClubItem(club = club, onClick = {
                            navController.navigate("clubDetails/${club.clubId}")
                        })
                    }
                }
            }
        }
    }
}