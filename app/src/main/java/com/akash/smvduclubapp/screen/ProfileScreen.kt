package com.akash.smvduclubapp.screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.Screen
import com.akash.smvduclubapp.data.User
import com.akash.smvduclubapp.data.deleteProfilePicture
import com.akash.smvduclubapp.data.fetchUser
import com.akash.smvduclubapp.data.updateUserAbout
import com.akash.smvduclubapp.data.uploadProfilePicture
import com.akash.smvduclubapp.viewmodel.AuthViewModel
import com.google.android.play.integrity.internal.f
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(
    navController: NavController,
    onNavigateToLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
) {
    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var about by remember { mutableStateOf("Hi! I'm using SMVDU Club App.") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isProfilePictureUpdating by remember { mutableStateOf(false) }
    var scrollState = rememberScrollState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            Log.d("ProfileScreen", "Image selected: $selectedUri")
            isProfilePictureUpdating = true
            scope.launch {
                try {
                    // Additional debug logs
                    Log.d("ProfileScreen", "Starting profile picture upload")
                    Log.d("ProfileScreen", "Current user: ${FirebaseAuth.getInstance().currentUser?.uid}")

                    val uploadedUrl = uploadProfilePicture(
                        context = context,
                        imageUri = selectedUri,
                        //username = user?.name ?: "user"
                    )

                    Log.d("ProfileScreen", "Upload result: $uploadedUrl")

                    if (uploadedUrl != null) {
                        // Update the display immediately after successful upload
                        profileImageUri = selectedUri
                        // Update the user object with the new profile picture URL
                        user = user?.copy(profile_picture = uploadedUrl)

                        Toast.makeText(
                            context,
                            "Profile picture updated",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.e("ProfileScreen", "Upload returned null URL")
                        Toast.makeText(
                            context,
                            "Failed to update profile picture. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e("ProfileScreen", "Error uploading profile picture", e)
                    Toast.makeText(
                        context,
                        "Error: ${e.message ?: "Unknown error"}",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    isProfilePictureUpdating = false
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val fetchedUser = fetchUser()
            user = fetchedUser
            // Set about, using default if null
            about = fetchedUser?.about ?: "Hi! I am using SMVDU Club App"
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error fetching user data", e)
            Toast.makeText(
                context,
                "Failed to load profile data",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            isLoading = false
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    scope.launch {
                        try {
                            //authViewModel.signOut()
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                            // Use the logout callback from NavigationGraph
                           onNavigateToLogout()
                        } catch (e: Exception) {
                            // Handle logout error
                            Toast.makeText(
                                context,
                                "Logout failed: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.theme_color),
                            Color(0xFF005F9E)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .clickable(enabled = isEditing) {
                                // Launch image picker when editing is enabled
                                launcher.launch("image/*")
                            }
                    ) {
                        when {
                            profileImageUri != null -> {
                                AsyncImage(
                                    model = profileImageUri,
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            !user?.profile_picture.isNullOrBlank() -> {
                                AsyncImage(
                                    model = user?.profile_picture,
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                    error = painterResource(id = R.drawable.profile_picture)
                                )
                            }
                            else -> {
                                Image(
                                    painter = painterResource(id = R.drawable.profile_picture),
                                    contentDescription = "Default Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // Editing overlay
                        if (isEditing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_camera_alt_24),
                                    contentDescription = "Edit Profile Picture",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                            ) {
                                // Profile image with border
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .border(4.dp, Color.White, CircleShape)
                                        .clickable(enabled = isEditing) {
                                            // Launch image picker when editing is enabled
                                            launcher.launch("image/*")
                                        }
                                ) {
                                    // Profile picture
                                    when {
                                        profileImageUri != null -> {
                                            AsyncImage(
                                                model = profileImageUri,
                                                contentDescription = "Profile Picture",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        !user?.profile_picture.isNullOrBlank() -> {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(user?.profile_picture)
                                                    .diskCachePolicy(CachePolicy.DISABLED)
                                                    .memoryCachePolicy(CachePolicy.DISABLED)
                                                    .build(),
                                                contentDescription = "Profile Picture",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize(),
                                                error = painterResource(id = R.drawable.profile_picture)
                                            )
                                        }
                                        else -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.profile_picture),
                                                contentDescription = "Default Profile Picture",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }

                                    // Editing overlay
                                    if (isEditing) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_camera_alt_24),
                                                contentDescription = "Edit Profile Picture",
                                                tint = Color.White,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }
                                }

                                // Delete profile picture badge - positioned OUTSIDE the inner Box but inside the outer Box
                                if (isEditing && !user?.profile_picture.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = (-16).dp, y = (14).dp) // Position it slightly outside the border
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .zIndex(1f)
                                            .background(Color.Red)
                                            .clickable {
                                                // Handle profile picture deletion
                                                scope.launch {
                                                    isLoading = true
                                                    try {
                                                        val deleteSuccessful = deleteProfilePicture()
                                                        if (deleteSuccessful) {
                                                            // Update local user state
                                                            user = user?.copy(profile_picture = "")
                                                            profileImageUri = null
                                                            Toast.makeText(
                                                                context,
                                                                "Profile picture removed",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Failed to remove profile picture",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    } finally {
                                                        isLoading = false
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Profile Picture",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.name ?: "User Name",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.05f))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.elevatedCardElevation(6.dp),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.cardcolor))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                EditableProfileField(
                    icon = Icons.Default.Person,
                    label = "Name",
                    value = user?.name ?: "",
                    onValueChange = {}, // No-op
                    isEditing = false,  // Not editable
                    isBold = true
                )
                EditableProfileField(
                    icon = Icons.Default.Info,
                    label = "About",
                    value = about,
                    onValueChange = { about = it },
                    isEditing = isEditing,
                    isBold = true
                )
                EditableProfileField(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = user?.email ?: "",
                    onValueChange = {}, // No-op
                    isEditing = false,  // Not editable
                    isBold = true
                )
            }
        }
        Spacer(modifier = Modifier.fillMaxHeight(0.05f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .clickable { /* Navigate to My Club screen */
                        navController.navigate(Screen.MyClubScreen.route)
                    },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.theme_color).copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_group_24),
                        contentDescription = "My Club Icon",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "My Clubs",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .clickable { /* Navigate to My Events screen */

                    navController.navigate(Screen.MyEventScreen.route)
                    },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.theme_color).copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_event_24),
                        contentDescription = "My Events Icon",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "My Events",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (isEditing) {
                    // Save changes to database
                    isLoading = true
                    scope.launch {
                        try {
                            val updateSuccessful = updateUserAbout(about)
                            if (updateSuccessful) {
                                Toast.makeText(
                                    context,
                                    "About updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to update about",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                }
                isEditing = !isEditing
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Icon(
                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isEditing) "Save Changes" else "Edit Profile",
                fontSize = 18.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.button2color))
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_logout_24),
                contentDescription = "Logout Icon",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Logout", fontSize = 18.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Powered by",
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = "Akash (23bcs010)",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
                    .offset(y = (-14).dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// Reusable Editable Profile Field
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableProfileField(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    isBold: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFEAEAEA), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF007BFF),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        if (isEditing) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 16.sp, color = Color.White),
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color(0xFF007BFF),
                    unfocusedIndicatorColor = Color.Gray
                )
            )
        } else {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                color = Color.Black
            )
        }
    }
}




