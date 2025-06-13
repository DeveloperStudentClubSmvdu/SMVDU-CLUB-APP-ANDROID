@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)
package com.akash.smvduclubapp.data

import android.util.Log
import com.akash.smvduclubapp.database.supabase
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlin.text.get


@Serializable
data class Club(
   val clubId: String,
   // val departmentId: String?,
    val name: String,
   // val email: String?,
    val description: String,
   // val coordinator: String?,
   /// val coCoordinator: String?,
   /// val createdAt: String?,
  //  val clubTitle: String?,
    val club_logo: String,
    val club_category: Long,
)

@Serializable
data class UserClubRelation(
    val id: Long? = null,
    val created_at: String? = null,
    val user_id: String? = null,
    val club_id: String? = null
)


suspend fun fetchClubs(): List<Club> {
    return try {
        supabase.from("clubs")
            .select()
            .decodeList<Club>()  // Fetch everything as Map

    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

suspend fun fetchClubsByCategory(categoryId: Long): List<Club> =
    supabase.from("clubs")
        .select{
            filter{
                eq("club_category", categoryId)
            }
        }
        .decodeList<Club>()


suspend fun saveUserClubRelationToSupabase(userId: String, clubId: String) {
    try {
        // Use the existing supabase client instead of creating a new one
        val response = supabase.postgrest["user_club_relation"]
            .insert(
                mapOf(
                    "user_id" to userId,
                    "club_id" to clubId
                )
            )
        Log.d("Supabase", "Saved user-club relation: $userId - $clubId")
    } catch (e: Exception) {
        Log.e("Supabase", "Error saving user-club relation: ${e.message}")
        throw e
    }
}

suspend fun fetchUserClubs(userId: String): List<Club> {
    try {
        Log.d("Supabase", "Fetching clubs for user: $userId")

        // Get user-club relations
        val userClubResponse = supabase.from("user_club_relation")
            .select{
            filter {
                eq("user_id", userId)
            }
            }
            .decodeList<UserClubRelation>()

        Log.d("Supabase", "Found ${userClubResponse.size} club relations")

        if (userClubResponse.isEmpty()) {
            return emptyList()
        }

        // Get all clubIds
        val clubIds = userClubResponse.mapNotNull { it.club_id }
        Log.d("Supabase", "Club IDs: $clubIds")

        // Fetch clubs
        val clubs = mutableListOf<Club>()
        for (clubId in clubIds) {
            try {
                val club = supabase.from("clubs")
                    .select{
                        filter {
                            eq("clubId", clubId)
                        }
                    }
                    .decodeSingle<Club>()
                clubs.add(club)
                Log.d("Supabase", "Successfully fetched club: ${club.name}")
            } catch (e: Exception) {
                Log.e("Supabase", "Error fetching club $clubId: ${e.message}")
            }
        }

        return clubs
    } catch (e: Exception) {
        Log.e("Supabase", "Error in fetchUserClubs: ${e.message}", e)
        return emptyList()
    }
}

// Add this data class to handle the relation table
