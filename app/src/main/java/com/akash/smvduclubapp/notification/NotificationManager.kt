@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)
package com.akash.smvduclubapp.notification

import android.util.Log
import com.akash.smvduclubapp.database.supabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.tasks.await

class NotificationManager {
    companion object {
        private const val TAG = "NotificationManager"

        suspend fun registerDeviceToken() {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

                // Store the token in Supabase
                supabase.from("user_devices")
                    .upsert(
                        mapOf(
                            "user_id" to userId,
                            "fcm_token" to token,
                            "device_type" to "android"
                        )
                    )

                Log.d(TAG, "Device token registered successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error registering device token", e)
            }
        }

        suspend fun unregisterDeviceToken() {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

                // Remove the token from Supabase
                supabase.from("user_devices")
                    .delete {
                        filter {
                            eq("user_id", userId)
                        }
                    }

                Log.d(TAG, "Device token unregistered successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering device token", e)
            }
        }
    }
} 