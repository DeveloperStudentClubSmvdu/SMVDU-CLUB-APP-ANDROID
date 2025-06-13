@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)
package com.akash.smvduclubapp.data
import com.akash.smvduclubapp.database.supabase
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class ClubCategory(
    val id: Long,
    val created_at: String,
    val name: String? = null,
    val description: String? = null,
    val logo: String? = null,
    val head_name: String? = null,
    val head_image: String? = null,
    val head_message: String? = null,
    val head_designation: String? = null,
)

suspend fun fetchClubCategories(): List<ClubCategory> =
    supabase.from("club_category")
        .select()
        .decodeList<ClubCategory>()