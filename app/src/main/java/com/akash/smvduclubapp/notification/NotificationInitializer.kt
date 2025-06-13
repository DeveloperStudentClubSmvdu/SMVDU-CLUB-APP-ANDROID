package com.akash.smvduclubapp.notification

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationInitializer(private val context: Context) {
    private val TAG = "NotificationInitializer"

    fun initialize() {
        // Request notification permission for Android 13 and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            if (!notificationManager.areNotificationsEnabled()) {
                // Show a dialog or navigate to settings to request permission
                Log.d(TAG, "Notifications not enabled")
                return
            }
        }

        // Enable FCM auto initialization
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        // Get FCM token and register it
        CoroutineScope(Dispatchers.IO).launch {
            try {
                NotificationManager.registerDeviceToken()
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing notifications", e)
            }
        }
    }

    fun cleanup() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                NotificationManager.unregisterDeviceToken()
            } catch (e: Exception) {
                Log.e(TAG, "Error cleaning up notifications", e)
            }
        }
    }
} 