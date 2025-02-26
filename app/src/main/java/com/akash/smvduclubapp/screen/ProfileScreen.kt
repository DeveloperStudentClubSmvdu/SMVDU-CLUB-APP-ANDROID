package com.akash.smvduclubapp.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.Screen


@Composable
fun ProfileScreen(navController: NavController, onNavigateToLogout: () -> Unit) {
    var isEditing by remember { mutableStateOf(false) } // Toggle Edit Mode
    var name by remember { mutableStateOf("Akash Kumar Chaurasiya") }
    var about by remember { mutableStateOf("Android Developer | AI Enthusiast") }
    var email by remember { mutableStateOf("akash@example.com") }
    var profileImage by remember { mutableStateOf(R.drawable.profile_picture) } // Default Image


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner Box
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .clickable {
                            // Handle Profile Image Change (Add Image Picker Later)
                            profileImage = R.drawable.new_profile_picture // Example Change
                        }
                ) {
                    Image(
                        painter = painterResource(id = profileImage),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize()
                    )
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
                Spacer(modifier = Modifier.height(10.dp))

                // Display Name in Header (Always Matches Below Name)
                Text(
                    text = name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Editable User Info
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
                    value = name,
                    onValueChange = { name = it },
                    isEditing = isEditing,
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
                    value = email,
                    onValueChange = { email = it },
                    isEditing = isEditing,
                    isBold = true
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Edit Profile Button
        Button(
            onClick = { isEditing = !isEditing },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green for edit
        ) {
            Icon(
                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                contentDescription = "Edit Profile",
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

        // Logout Button
        Button(
            onClick = {
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.ProfileScreen.route) { inclusive = true } // Clears ProfileScreen from the stack
                    launchSingleTop = true
                }
            },
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
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
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




