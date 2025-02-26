package com.akash.smvduclubapp.screen

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Club
import com.akash.smvduclubapp.data.Club.Companion.getDummyClubs


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") } // Search query state
    val allClubs = remember { getDummyClubs() } // Fetch all clubs
    val filteredClubs = remember(searchQuery) {
        allClubs.filter { club ->
            club.name.contains(searchQuery, ignoreCase = true) ||
                    club.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(4.dp)
    ) {
        // Modern Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp)), // Light background
            shape = RoundedCornerShape(24.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.club_search) ,
                    color = Color.Gray,
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
                focusedBorderColor = colorResource(R.color.theme_color), // Blue focus border
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color.Black
            )
        )

        // Clubs List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredClubs) { club ->
                ClubItem(club = club, onClick = {
                    // Navigate to Club Detail Page
                    navController.navigate("clubDetails/${club.name}")
                })
            }
        }
    }
}

@Composable
fun ClubItem(club: Club, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(colorResource(R.color.clubcardcolor))
            .clickable(onClick = onClick) // Make item clickable
            .padding(16.dp)
            .animateContentSize() // Smooth animation when content changes
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = club.logoResId),
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
                        color = Color.Black
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
