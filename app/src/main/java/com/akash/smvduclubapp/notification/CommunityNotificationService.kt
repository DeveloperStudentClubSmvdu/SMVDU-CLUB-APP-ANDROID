package com.akash.smvduclubapp.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.akash.smvduclubapp.R

class CommunityNotificationService(
    private val context: Context
) {
    fun showPostSuccessNotification() {
        // Debug logging
        android.util.Log.d("NotificationDebug", "Attempting to show notification")

        if (!checkNotificationPermission()) {
            android.util.Log.e("CommunityNotification", "Notification permission not granted")
            return
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Success!")
            .setContentText("Community feed sent successfully")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(0, 250, 250, 250))

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(NOTIFICATION_ID, builder.build())
            android.util.Log.d("CommunityNotification", "Notification sent successfully")
        } catch (e: Exception) {
            android.util.Log.e("NotificationDebug", "Failed to show notification", e)
        }
    }

    private fun checkNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    companion object {
        const val CHANNEL_ID = "community_channel"
        const val CHANNEL_NAME = "Community Notifications"
        const val CHANNEL_DESCRIPTION = "Notifications for community activities"
        const val NOTIFICATION_ID = 1
    }
}