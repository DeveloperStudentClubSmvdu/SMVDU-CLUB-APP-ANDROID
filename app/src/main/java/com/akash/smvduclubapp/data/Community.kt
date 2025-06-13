@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)

package com.akash.smvduclubapp.data

import android.util.Log
import android.widget.Toast
import com.akash.smvduclubapp.database.supabase
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

@Serializable
data class CommunityPost(
    val id: String,
    val uid: String?,
    val name: String,
    val post: String, // Match column name exactly
    val created_at: String // Ensure this matches the database column name
)


suspend fun fetchCommunityPosts(): List<CommunityPost> =
    supabase.from("community_posts")
        .select ()
        .decodeList<CommunityPost>()
        .sortedByDescending { it.created_at } // Sort by created_at in descending order



suspend fun fetchUserName(userId: String): String? {
    return try {
        val user = supabase.from("users")
            .select{
                filter {
                    eq("uid", userId)
                }
            }
            .decodeSingleOrNull<User>()

        return user?.name
    } catch (e: Exception) {
        Log.e("Supabase", "Error fetching user name: ${e.message}")
        null
    }
}

// Function to add a new community post to Supabase
suspend fun addCommunityPost(context: android.content.Context, userId: String?, userName: String, postContent: String): Boolean {

    if (userId == null) {
        Log.e("Community", "Cannot add post: User not logged in")
        Toast.makeText(
            context,
            "Please log in to add a post",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    return try {
        val post = CommunityPost(
            id = UUID.randomUUID().toString(),
            uid = userId,
            name = userName,
            post = postContent,
            created_at = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .format(Date())
        )

        supabase.from("community_posts")
            .insert(post)
           // .execute()

        true
    } catch (e: Exception) {
        Log.e("Supabase", "Error adding post: ${e.message}")
        false
    }
}