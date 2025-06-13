package com.akash.smvduclubapp.screen.mainscreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.akash.smvduclubapp.Screen
import com.akash.smvduclubapp.data.BottomNavItem

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = BottomNavItem.dummyData()

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