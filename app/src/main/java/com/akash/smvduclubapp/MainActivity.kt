package com.akash.smvduclubapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.akash.smvduclubapp.data.NotificationData
import com.akash.smvduclubapp.ui.theme.SMVDUClubAppTheme
import com.akash.smvduclubapp.viewmodel.AuthViewModel
import com.akash.smvduclubapp.notification.NotificationInitializer
import kotlin.text.compareTo



class MainActivity : ComponentActivity() {
    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 101
    }
    private lateinit var notificationInitializer: NotificationInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize notification system
        notificationInitializer = NotificationInitializer(this)
        notificationInitializer.initialize()



        // Handle notification intents
        handleNotificationIntent(intent)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val authViewModel : AuthViewModel = viewModel()
            SMVDUClubAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    NavigationGraph(
                        navController = navController,
                        authViewModel =authViewModel
                    )


                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?,) {
        intent?.let {
            when (it.getStringExtra("type")) {
                "chat" -> {
                    val chatRoomId = it.getStringExtra("chatRoomId")
                    if (chatRoomId != null) {
                        // Navigate to chat room
                        // You'll need to implement this based on your navigation setup

                    }
                }
                "event" -> {
                    val eventId = it.getStringExtra("eventId")
                    if (eventId != null) {
                        // Navigate to event details
                        // You'll need to implement this based on your navigation setup
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationInitializer.cleanup()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
    }
}



