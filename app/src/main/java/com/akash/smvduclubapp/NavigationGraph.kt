package com.akash.smvduclubapp

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.akash.smvduclubapp.data.fetchUser
import com.akash.smvduclubapp.screen.ChatScreen
import com.akash.smvduclubapp.screen.ClubDetailScreen
import com.akash.smvduclubapp.screen.ClubScreen
import com.akash.smvduclubapp.screen.EventDetailScreen
import com.akash.smvduclubapp.screen.EventListScreen
import com.akash.smvduclubapp.screen.EventRegistrationForm
import com.akash.smvduclubapp.screen.FestDetailScreen
import com.akash.smvduclubapp.screen.ForgetPasswordScreen
import com.akash.smvduclubapp.screen.LoginScreen
import com.akash.smvduclubapp.screen.MyClubScreen
import com.akash.smvduclubapp.screen.MyEventsScreen
import com.akash.smvduclubapp.screen.NotificationScreen
import com.akash.smvduclubapp.screen.ProfileScreen
import com.akash.smvduclubapp.screen.SignUpScreen
import com.akash.smvduclubapp.screen.SplashScreen
import com.akash.smvduclubapp.screen.mainscreen.ActivityHeadScreen
import com.akash.smvduclubapp.screen.mainscreen.BottomNavigationBar
import com.akash.smvduclubapp.screen.mainscreen.HomeTopBar
import com.akash.smvduclubapp.screen.mainscreen.MainScreen
import com.akash.smvduclubapp.screen.mainscreen.VCMessageScreen
import com.akash.smvduclubapp.viewmodel.AuthViewModel
import com.akash.smvduclubapp.viewmodel.PasswordResetViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val chatRoomTitle = remember { mutableStateOf("Chat") }
    val chatRoomLogo = remember { mutableStateOf<String?>(null) }

    val screenTitle = when (currentRoute) {
        Screen.MainScreen.route -> "Home"
        Screen.ClubScreen.route -> "Clubs"
        Screen.EventListScreen.route -> "Events"
        Screen.ProfileScreen.route -> "Profile"
        Screen.MyClubScreen.route -> "My Clubs"
        Screen.MyEventScreen.route -> "My Events"
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
        Screen.MyClubScreen.route,
        Screen.MyEventScreen.route,
        "chatRoom/{roomId}"
    )

    Scaffold(
        topBar = {
            if (showTopBar) {
                val title = if (currentRoute?.startsWith("chatRoom/") == true) {
                    chatRoomTitle.value
                } else {
                    screenTitle
                }

                val logo = if (currentRoute?.startsWith("chatRoom/") == true) {
                    chatRoomLogo.value
                } else {
                    null
                }

                HomeTopBar(
                    title = title,
                    clubLogo = logo,
                    onNotificationClick = {
                        navController.navigate(Screen.NotificationScreen.route)
                    }
                )
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
                        onNavigateToLogin = {
                            navController.navigate(Screen.LoginScreen.route) {
                                // Pop signup screen so we can't go back to it
                                popUpTo(Screen.SignupScreen.route) {
                                    inclusive = true
                                }
                            }
                        },
                        onNavigateToMain = {
                            // Navigate to MainScreen and clear the entire back stack
                            navController.navigate(Screen.MainScreen.route) {
                                // Clear the entire back stack so user can't go back to any auth screens
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                composable(Screen.LoginScreen.route) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onNavigateToForgotPassword = { navController.navigate("forgotPassword") },
                        onNavigateToSignUp = {
                            navController.navigate(Screen.SignupScreen.route) {
                                // Pop login screen so we can't go back to it from signup
                                popUpTo(Screen.LoginScreen.route) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        // Navigate to MainScreen and clear the entire back stack
                        navController.navigate(Screen.MainScreen.route) {
                            // Clear the entire back stack
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }

                }
                composable("forgotPassword") {
                    val passwordResetViewModel: PasswordResetViewModel = viewModel()
                    ForgetPasswordScreen(
                        passwordResetViewModel = passwordResetViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("vcMessage") {
                    VCMessageScreen(navController)
                }
                composable(
                    route = "activity_head_screen/{categoryId}",
                    arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: 0L
                    ActivityHeadScreen(navController = navController, categoryId = categoryId)
                }
                composable(Screen.MainScreen.route) { backStackEntry ->
                    MainScreen(navController)
                }
                composable(Screen.MyClubScreen.route) {
                    MyClubScreen(navController = navController)
                }
                composable(Screen.MyEventScreen.route) {
                    // Pass the navController to your screen
                    MyEventsScreen(navController = navController)
                }

                composable(Screen.ClubScreen.route) {
                    ClubScreen(navController)
                }

                composable(Screen.EventListScreen.route) {
                    EventListScreen(navController)
                }

                composable(Screen.ProfileScreen.route) {
                    ProfileScreen(
                        navController,
                        onNavigateToLogout = {
                            Log.d("ProfileScreen", "onNavigateToLogout called")
                            authViewModel.signOut() // Make sure to implement this method
                            val context = navController.context

                            // Create a new intent for MainActivity with clear flags
                            val intent = Intent(context, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }

                            // Start the activity fresh
                            context.startActivity(intent)
                        }
                    )
                }

                composable(Screen.NotificationScreen.route) {
                    NotificationScreen(navController = navController)
                }

                composable(
                    route = "eventregistrationform/{eventName}/{eventId}",
                    arguments = listOf(navArgument("eventName") { type = NavType.StringType })
                        + navArgument("eventId") { type = NavType.StringType }
                ) {
                    val eventName = it.arguments?.getString("eventName")
                    val eventId = it.arguments?.getString("eventId")
                    EventRegistrationForm(navController=navController,eventName,eventId)
                }

                composable("clubDetails/{clubId}") { backStackEntry ->
                    val clubId = backStackEntry.arguments?.getString("clubId")
                    ClubDetailScreen(navController = navController, clubId = clubId)
                }

                // Add new route for chat room
                composable("chatRoom/{roomId}") { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                    var userName by remember { mutableStateOf("New User") }
                    val scope = rememberCoroutineScope()

                    // Fetch the username when the composable is first launched
                    LaunchedEffect(Unit) {
                        scope.launch {
                            try {
                                val user = fetchUser()
                                user?.let {
                                    userName = it.name.ifEmpty {
                                        "New User"
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("NavigationGraph", "Error fetching user: ${e.message}")
                            }
                        }
                    }

                    ChatScreen(
                        navController = navController,
                        roomId = roomId,
                        userName = userName,
                        onChatRoomLoaded = { chatRoom ->
                            // Update the TopBar info when chat room is loaded
                            chatRoom?.let {
                                chatRoomTitle.value = it.clubName ?: "Chat"
                                chatRoomLogo.value = it.clubLogo
                            }
                        }
                    )
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