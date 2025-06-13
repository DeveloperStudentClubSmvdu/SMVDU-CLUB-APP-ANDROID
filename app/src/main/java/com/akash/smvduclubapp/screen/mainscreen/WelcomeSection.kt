package com.akash.smvduclubapp.screen.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.User
import com.akash.smvduclubapp.data.fetchUser
import kotlinx.coroutines.launch


@Composable
fun WelcomeSection() {
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }

    // Fetch user details when the screen is launched
    LaunchedEffect(Unit) {
        scope.launch {
            user = fetchUser()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary), // Set card color
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Text(
                text = "👋 Welcome, ${user?.name ?: "Guest"}!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary, // Ensure text is visible
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = stringResource(id = R.string.welcome_description),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f), // Color(0xFFB0B0B0) // Adjust contrast for readability

            )
        }
    }
}
