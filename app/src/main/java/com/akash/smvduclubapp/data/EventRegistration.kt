@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)
package com.akash.smvduclubapp.data

import android.util.Log
import com.akash.smvduclubapp.database.supabase
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlin.Result
import kotlin.text.insert
import io.github.jan.supabase.postgrest.rpc
import kotlin.text.insert

@Serializable
data class EventRegistration(
    val id: String? = null,
    val event_id: String,
    val event_name: String,
    val name: String,
    val entry_no: String = "",
    val email: String,
    val phone: String,
    val department: String,
    val registration_date: String = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())
)

suspend fun registerForEvent(registration: EventRegistration): Result<Boolean> {
    // Create a sanitized table name using event name and ID
    val sanitizedEventName = registration.event_name
        .replace(Regex("[^A-Za-z0-9]"), "_")
        .lowercase()
        .take(50) // Limit length to avoid excessively long table names

    val tableName = "event_${sanitizedEventName}"

    return try {
        // First, create the table if it doesn't exist using RPC
        supabase.postgrest.rpc(
            function = "create_event_registration_table",
            parameters = mapOf("table_name" to tableName)
        )

        // Insert the registration data into the dynamic table
        supabase.from(tableName).insert(registration)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""
        // Also insert into event_user_relation
        supabase.from("event_user_relation").insert(
            mapOf(
                "id" to java.util.UUID.randomUUID().toString(),
                "event_id" to registration.event_id,
                "user_id" to userId
            )
        )

        Result.success(true)
    } catch (e: Exception) {
        Log.e("EventRegistration", "Error registering for event: ${e.message}", e)
        Result.failure(e)
    }
}

@Serializable
data class EventUserRelation(
    val id: String,
    val event_id: String,
    val user_id: String
)

suspend fun isUserRegisteredForEvent(eventId: String): Boolean {
    return try {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        val results = supabase.from("event_user_relation")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("event_id", eventId)
                }
            }
            .decodeList<EventUserRelation>()

        val count = results.size

        count > 0
    } catch (e: Exception) {
        Log.e("EventRegistration", "Error checking registration status: ${e.message}", e)
        false
    }
}