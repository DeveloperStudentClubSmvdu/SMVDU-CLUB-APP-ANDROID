package com.akash.smvduclubapp.screen.mainscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.CommunityPost
import com.akash.smvduclubapp.data.addCommunityPost
import com.akash.smvduclubapp.data.fetchCommunityPosts
import com.akash.smvduclubapp.data.fetchUserName
import com.akash.smvduclubapp.notification.CommunityNotificationService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeedSection() {
    val scope = rememberCoroutineScope()
    var posts by remember { mutableStateOf<List<CommunityPost>>(emptyList()) }
    var usernames by remember { mutableStateOf<Map<String?, String>>(mapOf()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddPostDialog by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        scope.launch {
            try {
                posts = fetchCommunityPosts()

                // Fetch username for each post
                val usernameMap = mutableMapOf<String?, String>()
                posts.forEach { post ->
                    post.uid?.let { uid ->
                        val name = fetchUserName(uid) ?: "Unknown User"
                        usernameMap[uid] = name
                    }
                }
                usernames = usernameMap
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching posts list: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💬 Community Feed",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            IconButton(
                onClick = { showAddPostDialog = true },
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Post",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            posts.take(3).forEach { post -> // Display only the first 3 posts
                val displayName = post.uid?.let { usernames[it] } ?: post.name
                PostCard(username = displayName, postContent = post.post)
            }
        }
    }

    if (showAddPostDialog) {
        AddPostDialog(
            onDismiss = { showAddPostDialog = false },
            onPostAdded = {
                // Refresh the posts list after adding a new post
                scope.launch {
                    try {
                        posts = fetchCommunityPosts()
                        // Update username map for new posts
                        val usernameMap = usernames.toMutableMap()
                        posts.forEach { post ->
                            post.uid?.let { uid ->
                                if (!usernameMap.containsKey(uid)) {
                                    val name = fetchUserName(uid) ?: "Unknown User"
                                    usernameMap[uid] = name
                                }
                            }
                        }
                        usernames = usernameMap
                    } catch (e: Exception) {
                        Log.e("Supabase", "Error refreshing posts: ${e.message}")
                    }
                }
            }
        )
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = username, fontWeight = FontWeight.Bold, fontSize = 16.sp,)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = postContent, fontSize = 14.sp,)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostDialog(onDismiss: () -> Unit, onPostAdded: () -> Unit) {
    var postContent by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.share_with_community),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // User profile info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Face,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = currentUser?.displayName ?: "Anonymous User",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Input field with rounded corners
                OutlinedTextField(
                    value = postContent,
                    onValueChange = {
                        if (it.length <= 500) postContent = it
                    },
                    label = { Text(stringResource(R.string.what_s_on_your_mind)) },
                    placeholder = { Text(stringResource(R.string.share_something_interesting)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    maxLines = 7
                )

                // Character counter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${postContent.length}/500",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (postContent.length > 450)
                            Color.Red.copy(alpha = 0.8f) else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Progress indicator
                if (isSubmitting) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.sharing_your_post),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (postContent.isNotBlank()) {
                        isSubmitting = true
                        scope.launch {
                            try {
                                val userId = currentUser?.uid
                                val userName = currentUser?.displayName ?: context.getString(R.string.anonymous_user)
                                val success = addCommunityPost(context,userId, userName, postContent)

                                if (success) {
                                    val notificationService = CommunityNotificationService(context)
                                    notificationService.showPostSuccessNotification()
                                    onPostAdded()
                                    onDismiss()
                                } else {
                                    Log.e("CommunityPost", "Failed to add post")
                                }
                            } catch (e: Exception) {
                                Log.e("CommunityPost", "Error adding post: ${e.message}")
                            } finally {
                                isSubmitting = false
                            }
                        }
                    }
                },
                enabled = postContent.isNotBlank() && !isSubmitting,
                modifier = Modifier.padding(horizontal = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.share_post))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
