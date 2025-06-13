@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)
package com.akash.smvduclubapp.data

import com.akash.smvduclubapp.database.supabase
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class Fest(
    val id: String,
    val name: String,
    val fest_date: String,
    val description: String,
    val fest_logo: String
)


suspend fun fetchFests(): List<Fest> {
    return try {
        supabase.from("university_fest")
            .select()
            .decodeList<Fest>()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}