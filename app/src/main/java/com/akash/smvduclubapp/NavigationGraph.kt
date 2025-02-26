package com.akash.smvduclubapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.akash.smvduclubapp.screen.BottomNavigationBar
import com.akash.smvduclubapp.screen.ClubDetailScreen
import com.akash.smvduclubapp.screen.ClubScreen
import com.akash.smvduclubapp.screen.EventDetailScreen
import com.akash.smvduclubapp.screen.EventListScreen
import com.akash.smvduclubapp.screen.HomeTopBar
import com.akash.smvduclubapp.screen.LoginScreen
import com.akash.smvduclubapp.screen.MainScreen
import com.akash.smvduclubapp.screen.NotificationScreen
import com.akash.smvduclubapp.screen.ProfileScreen
import com.akash.smvduclubapp.screen.SignUpScreen
import com.akash.smvduclubapp.screen.SplashScreen
import com.akash.smvduclubapp.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue
import com.akash.smvduclubapp.screen.FestDetailScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val screenTitle = when (currentRoute) {
        Screen.MainScreen.route -> "Home"
        Screen.ClubScreen.route -> "Clubs"
        Screen.EventListScreen.route -> "Upcoming Events"
        Screen.ProfileScreen.route -> "Profile"
        else -> "SMVDU Clubs" // Default title
    }
    val showBottomBar = currentRoute in listOf(
        Screen.MainScreen.route,
        Screen.ClubScreen.route,
        Screen.EventListScreen.route,
        Screen.ProfileScreen.route
    )
    val showTopBar = currentRoute in listOf(
        Screen.MainScreen.route,
        Screen.ClubScreen.route,
        Screen.EventListScreen.route,
    )



    Scaffold(
        topBar = {
            if (showTopBar) {
                HomeTopBar(title =screenTitle ) {
                    navController.navigate(Screen.NotificationScreen.route)
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Screen.SplashScreen.route,
                modifier = modifier
            ) {
                composable(Screen.SplashScreen.route) {
                    SplashScreen {
                        // Check authentication state
                        val isUserLoggedIn =
                            authViewModel.isUserAuthenticated() // Implement this logic in ViewModel

                        if (isUserLoggedIn) {
                            navController.navigate(Screen.MainScreen.route) {
                                popUpTo(Screen.SplashScreen.route) {
                                    inclusive = true
                                } // Remove splash from backstack
                            }
                        } else {
                            navController.navigate(Screen.LoginScreen.route) {
                                popUpTo(Screen.SplashScreen.route) {
                                    inclusive = true
                                } // Remove splash from backstack
                            }
                        }
                    }
                }



                composable(Screen.SignupScreen.route) {
                    SignUpScreen(
                        authViewModel = authViewModel,
                        onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) },
                        onNavigateToMain = { navController.navigate(Screen.MainScreen.route) }
                    )
                }
                composable(Screen.LoginScreen.route) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) }
                    ) {
                        navController.navigate(Screen.MainScreen.route)
                    }
                }
                composable(Screen.MainScreen.route) { backStackEntry ->
                    val userName = backStackEntry.arguments?.getString("userName") ?: "Guest"
                    MainScreen(navController, userName)
                }
                composable(Screen.ClubScreen.route) {
                    ClubScreen(navController)
                }
                composable(Screen.EventListScreen.route) {
                    EventListScreen(navController)
                }
                composable(Screen.ProfileScreen.route) {
                    ProfileScreen(navController,
                        onNavigateToLogout = { navController.navigate(Screen.LoginScreen.route) })
                }

                composable(Screen.NotificationScreen.route) {
                    NotificationScreen(navController = navController)
                }

                composable("clubDetails/{clubName}") { backStackEntry ->
                    val clubName = backStackEntry.arguments?.getString("clubName")
                    ClubDetailScreen(navController, clubName)
                }
                composable("eventDetails/{eventTitle}") { backStackEntry ->
                    val eventTitle = backStackEntry.arguments?.getString("eventTitle")
                    EventDetailScreen(navController, eventTitle)
                }
                composable("festDetail/{festName}") { backStackEntry ->
                    val festName = backStackEntry.arguments?.getString("festName")
                    FestDetailScreen(navController, festName)
                }
            }
        }
    }
}
