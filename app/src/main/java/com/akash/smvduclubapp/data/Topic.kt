
@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)package com.akash.smvduclubapp.data

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.akash.smvduclubapp.database.supabase
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*


// Custom serializer to handle integer ID as string
object IntAsStringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IntAsString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }

    override fun deserialize(decoder: Decoder): String {
        // First try to decode as integer, then convert to string
        return try {
            decoder.decodeInt().toString()
        } catch (e: Exception) {
            // If it fails, try as string directly
            decoder.decodeString()
        }
    }
}

// Updated data class with custom serializer
@Serializable
data class EventDescription(
    @Serializable(with = IntAsStringSerializer::class)
    val id: String,
    val created_at: String,
    val Topic1: String?,
    val Topic1_description: String?,
    val Topic2: String?,
    val Topic2_description: String?,
    val Topic3: String?,
    val Topic3_description: String?,
    val Topic4: String?,
    val Topic4_description: String?,
    val Topic5: String?,
    val Topic5_description: String?
)


// Helper data class for internal use
data class TopicData(
    val name: String,
    val description: String
)

// Predefined colors for the 5 topics
val topicColors = listOf(
    Color(0xFFFF3B30), // Red
    Color(0xFFFF9500), // Orange
    Color(0xFF007AFF), // Blue
    Color(0xFF4CAF50), // Green
    Color(0xFF673AB7)  // Purple
)



// The fetchEventDescription function remains unchanged
suspend fun fetchEventDescription(description_id: String): EventDescription? {
    return try {
        Log.d("FetchEventDesc", "Fetching event description with ID: $description_id")

        val result = supabase.from("event_description")
            .select {
                filter {
                    eq("id", description_id)
                }
            }
            .decodeSingle<EventDescription>()

        Log.d("FetchEventDesc", "Successfully fetched event description: ${result.id}")
        result
    } catch (e: Exception) {
        Log.e("FetchEventDesc", "Error fetching event description with ID $description_id: ${e.message}", e)
        e.printStackTrace()
        null
    }
}