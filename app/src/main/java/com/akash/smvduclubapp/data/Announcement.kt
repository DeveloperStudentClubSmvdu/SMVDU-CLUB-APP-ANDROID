@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)

package com.akash.smvduclubapp.data

import android.util.Log
import com.akash.smvduclubapp.database.supabase
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    val id : Int,
    val message: String
)

suspend fun fetchLatestAnnouncement(): Announcement? {
    return try {
        // Get all announcements and find the one with max ID client-side
        val announcements = supabase.from("announcements")
            .select()
            .decodeList<Announcement>()

        announcements.maxByOrNull { it.id }
    } catch (e: Exception) {
        Log.e("Supabase", "Error fetching latest announcement: ${e.message}")
        null
    }
}

