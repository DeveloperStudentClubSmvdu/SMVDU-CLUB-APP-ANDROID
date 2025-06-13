package com.akash.smvduclubapp.screen.mainscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.ClubCategory
import com.akash.smvduclubapp.data.fetchClubCategories
import kotlinx.coroutines.launch

@Composable
fun ActivityHeadSection(navController: NavController) {
    val scope = rememberCoroutineScope()
    var categories by remember { mutableStateOf<List<ClubCategory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        scope.launch {
            try {
                categories = fetchClubCategories()
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching club categories: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Text(
        text = "🏆 Explore SMVDU",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    } else {
        Column(modifier = Modifier.fillMaxWidth()) {
            categories.forEach { category ->
                ActivityHeadSectionCard(
                    title = category.name ?: stringResource(R.string.unnamed_club),
                    description = category.description ?: stringResource(R.string.no_description_available),
                    logoUrl = category.logo ?: "",
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        // Navigate to ActivityHeadScreen with the category ID
                        navController.navigate("activity_head_screen/${category.id}")
                    }
                )
            }
        }
    }
}


@Composable
fun ActivityHeadSectionCard(
    title: String,
    description: String,
    logoUrl: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (logoUrl.isNotBlank()) {
                AsyncImage(
                    model = logoUrl,
                    contentDescription = "$title logo",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(18.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description.take(100).trimEnd() + if (description.length > 100) "..." else "",
            fontSize = 14.sp,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.read_more),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onClick() }
                .padding(8.dp)
        )
    }
}