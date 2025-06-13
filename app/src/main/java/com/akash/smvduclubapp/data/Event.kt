@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)
package com.akash.smvduclubapp.data


import android.util.Log
import com.akash.smvduclubapp.database.supabase
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class Event(
    val id: String,
   //val department_id: String?,
    val club_id: String?,
    val fest_id: String?,
    val name: String,
    val description: String,
    val event_date: String,
    val event_time: String,
    val event_poster: String,
    val event_title: String,
    val event_venue: String,
    val description_id: Long? = null,
    val event_registration_date: String? = null,
    //val department_fest_id: String?,

)

suspend fun fetchEvents(): List<Event> {
    return try {
        supabase.from("events")
            .select()
            .decodeList<Event>()  // Fetch everything as Map
    } catch (e: Exception) {
        Log.e("FetchEvents", "Error fetching events", e)
        e.printStackTrace()
        emptyList()
    }
}

suspend fun fetchEventsByClubId(clubId: String): List<Event> {
    return try {
        supabase.from("events")
            .select {
                filter {
                    eq("club_id", clubId)
                }
            }
            .decodeList<Event>()
    } catch (e: Exception) {
        Log.e("FetchEvents", "Error fetching events for club $clubId", e)
        e.printStackTrace()
        emptyList()
    }
}

suspend fun fetchEventsByFestId(festId: String): List<Event> {
    return try {
        supabase.from("events")
            .select {
                filter {
                    eq("fest_id", festId)
                }
            }
            .decodeList<Event>()
    } catch (e: Exception) {
        Log.e("FetchEvents", "Error fetching events for fest $festId", e)
        e.printStackTrace()
        emptyList()
    }
}

suspend fun fetchUpcomingEvents(): List<Event> {
    // Get today's date in the format stored in your database (assuming YYYY-MM-DD)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    return try {
        supabase.from("events")
            .select {
                filter {
                    gte("event_date", today)
                }
                order("event_date", Order.ASCENDING) // Ascending order (true = ASC, false = DESC)
            }
            .decodeList<Event>()
    } catch (e: Exception) {
        Log.e("FetchEvents", "Error fetching upcoming events", e)
        e.printStackTrace()
        emptyList()
    }
}
// Add this function to the data package
// Add this data class to handle the event-user relation


suspend fun fetchUserEvents(userId: String): List<Event> {
    try {
        Log.d("Supabase", "Fetching events for user: $userId")

        // Get user-event relations
        val userEventResponse = supabase.from("event_user_relation")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<EventUserRelation>()

        Log.d("Supabase", "Found ${userEventResponse.size} event relations")

        if (userEventResponse.isEmpty()) {
            return emptyList()
        }

        // Get all eventIds
        val eventIds = userEventResponse.mapNotNull { it.event_id }
        Log.d("Supabase", "Event IDs: $eventIds")

        // Fetch events
        val events = mutableListOf<Event>()
        for (eventId in eventIds) {
            try {
                val event = supabase.from("events")
                    .select {
                        filter {
                            eq("id", eventId)
                        }
                    }
                    .decodeSingle<Event>()
                events.add(event)
                Log.d("Supabase", "Successfully fetched event: ${event.name}")
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching event $eventId: ${e.message}")
            }
        }

        return events
    } catch (e: Exception) {
        Log.e("Supabase", "Error in fetchUserEvents: ${e.message}", e)
        return emptyList()
    }
}