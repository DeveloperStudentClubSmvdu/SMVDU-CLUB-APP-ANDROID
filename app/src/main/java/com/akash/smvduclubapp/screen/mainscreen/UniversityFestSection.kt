package com.akash.smvduclubapp.screen.mainscreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Fest
import com.akash.smvduclubapp.data.fetchFests
import kotlinx.coroutines.launch

@Composable
fun UniversityFestSection(navController: NavController) {
    val scope = rememberCoroutineScope()
    var festList by remember { mutableStateOf<List<Fest>>(emptyList()) }

// Fetch fests when the Composable is launched
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(true) {
        scope.launch {
            try {
                festList = fetchFests()
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching fest list: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Text(
        text = "🎉 University Fest",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    } else{
        LazyRow(modifier = Modifier.padding(8.dp)) {
            items(festList) { fest ->
                FestCard(festName = fest.name, festDate = fest.fest_date, festDescription = fest.description, navController = navController)
            }
        }
    }
}

@Composable
fun FestCard(festName: String, festDate: String, festDescription: String, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(220.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = festName,fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary)
            Text(text = festDate, fontSize = 14.sp,fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f))
            Text(text = festDescription.take(100).trimEnd() + if (festDescription.length > 100) "..." else ""
                , fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
                )

            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { navController.navigate("festDetail/${festName}") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text(stringResource(R.string.more), color = Color.White)
            }
        }
    }
}
