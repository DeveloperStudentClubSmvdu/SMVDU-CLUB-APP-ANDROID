@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)
package com.akash.smvduclubapp.notification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.akash.smvduclubapp.MainActivity
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.NotificationData
import com.akash.smvduclubapp.database.NotificationRepository
import com.akash.smvduclubapp.database.supabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.apply
import kotlin.text.set

class FCMService : FirebaseMessagingService() {
    private val TAG = "FCMService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        
        // Register the new token in a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    // Store the token in Supabase
                    supabase.from("user_devices")
                        .upsert(
                            mapOf(
                                "user_id" to userId,
                                "fcm_token" to token,
                                "device_type" to "android"
                            )
                        )
                    Log.d(TAG, "New token registered successfully")
                } else {
                    Log.w(TAG, "User not logged in, token not registered")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error registering new token", e)
            }
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title ?: "New Notification", it.body ?: "")
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "New Notification"
        val body = data["body"] ?: ""
        val type = data["type"] ?: ""
        val itemId = data["itemId"] ?: ""

        // Create notification based on type
        when (type) {
            "chat" -> sendChatNotification(title, body, itemId)
            "event" -> sendEventNotification(title, body, itemId)
            else -> sendNotification(title, body)
        }
    }

    private fun sendChatNotification(title: String, body: String, chatRoomId: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("type", "chat")
            putExtra("chatRoomId", chatRoomId)
        }
        sendNotification(title, body, intent)
    }

    private fun sendEventNotification(title: String, body: String, eventId: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("type", "event")
            putExtra("eventId", eventId)
        }
        sendNotification(title, body, intent)
    }

    private fun sendNotification(title: String, body: String, intent: Intent? = null) {
        val defaultIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent ?: defaultIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

}