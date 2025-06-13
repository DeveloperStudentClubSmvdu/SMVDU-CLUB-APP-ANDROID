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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Club
import com.akash.smvduclubapp.data.fetchClubs
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") } // Search query state
    val scope = rememberCoroutineScope()
    var allClubs by remember { mutableStateOf<List<Club>>(emptyList()) }

    // Sorting state
    var isAlphabeticallySorted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                allClubs = fetchClubs().shuffled()
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching club list: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    val filteredClubs = remember(searchQuery, allClubs, isAlphabeticallySorted) {
        // First filter based on search query
        val filtered = if (searchQuery.isBlank()) allClubs
        else allClubs.filter { club ->
            club.name.contains(searchQuery, ignoreCase = true) ||
                    club.description?.contains(searchQuery, ignoreCase = true) == true
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
                        text = stringResource(R.string.club_search),
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

        if (isLoading) {
            // Show loading indicator while fetching data
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
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

@Composable
fun ClubItem(club: Club, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(15.dp),
                clip = false // true if you want to clip shadow to shape
            )
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onClick)
            .padding(16.dp)
            .animateContentSize() // Smooth animation when content changes
    )  {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(club.club_logo),
                contentDescription = "${club.name} Logo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = club.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                    )
                )
                Text(
                    text = club.description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}