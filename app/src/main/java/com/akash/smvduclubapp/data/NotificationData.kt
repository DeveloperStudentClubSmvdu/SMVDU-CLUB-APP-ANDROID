package com.akash.smvduclubapp.data

import android.R.attr.order
import android.util.Log
import com.akash.smvduclubapp.database.supabase
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class NotificationData(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val type: String = "general",
    val itemId: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object
}

@OptIn(SupabaseExperimental::class, SupabaseInternal::class)
suspend fun insertNotification(
    title: String,
    description: String,
    type: String = "general",
    itemId: String = ""
) {
    try {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        supabase.from("notifications")
            .insert(
                mapOf(
                    "title" to title,
                    "description" to description,
                    "date" to currentDate,
                    "time" to currentTime,
                    "type" to type,
                    "item_id" to itemId,
                    "is_read" to false
                )
            )
        Log.d("InsertNotification", "Notification inserted successfully")
    } catch (e: Exception) {
        Log.e("InsertNotification", "Error inserting notification", e)
    }
}

@OptIn(SupabaseExperimental::class, SupabaseInternal::class)
suspend fun fetchRecentNotifications(daysBack: Int = 7): List<NotificationData> {
    return try {
        supabase.from("notifications")
            .select()

            .decodeList<NotificationData>()
    } catch (e: Exception) {
        Log.e("FetchNotifications", "Error fetching notifications", e)
        e.printStackTrace()
        emptyList()
    }
}




