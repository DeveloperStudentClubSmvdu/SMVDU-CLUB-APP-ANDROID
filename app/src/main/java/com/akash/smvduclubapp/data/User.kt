@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)

package com.akash.smvduclubapp.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.akash.smvduclubapp.database.supabase
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.roundToInt

@Serializable
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val about: String? = null,
    @SerialName("profile_picture")
    val profile_picture: String? = null,
)

// Suspend function to fetch user with about section
suspend fun fetchUser(): User? {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid ?: return null

    return try {
        val result = supabase.from("users")
            .select {
                filter {
                    eq("uid", uid)
                }
            }
            .decodeSingleOrNull<User>()

        // If no user found, create a default user with basic info
        result ?: User(
            uid = uid,
            name = currentUser.displayName ?: "",
            email = currentUser.email ?: "",
            about = "Hi! I am using SMVDU Club App",
            profile_picture = "" // Empty default profile picture
        )
    } catch (e: Exception) {
        Log.e("Supabase", "Error fetching user: ${e.message}")
        null
    }
}

// Function to update user's about section in the database
suspend fun updateUserAbout(about: String): Boolean {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid ?: return false

    return try {
        supabase.from("users")
            .update({
                set("about", about)
            }) {
                filter {
                    eq("uid", uid)
                }
            }
        true
    } catch (e: Exception) {
        Log.e("Supabase", "Error updating about: ${e.message}")
        false
    }
}

suspend fun createProfilePicturesBucket(): Boolean {
    val bucketName = "profile-pictures"

    return try {
        // Check if bucket exists
        val buckets = supabase.storage.retrieveBuckets()
        val bucketExists = buckets.any { it.name == bucketName }

        if (!bucketExists) {
            Log.d("SupabaseStorage", "Creating bucket: $bucketName")

            // Create bucket with public access
            supabase.storage.createBucket(bucketName)

            Log.d("SupabaseStorage", "Bucket created successfully: $bucketName")
            true
        } else {
            Log.d("SupabaseStorage", "Bucket already exists: $bucketName")
            true
        }
    } catch (e: Exception) {
        Log.e("SupabaseStorage", "Error creating bucket: ${e.message}", e)
        false
    }
}

// Updated profile picture upload function that ensures bucket exists
// Updated profile picture upload function that deletes the old picture
suspend fun uploadProfilePicture(
    context: Context,
    imageUri: Uri,
): String? {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid ?: return null
    val bucketName = "profile-pictures"

    return try {
        // First, ensure the bucket exists
        val bucketCreated = createProfilePicturesBucket()
        if (!bucketCreated) {
            Log.e("ProfilePictureUpload", "Failed to create/verify bucket: $bucketName")
            return null
        }

        // Get current user to check if they have an existing profile picture
        val user = fetchUser()

        // Delete the old profile picture if it exists
        user?.profile_picture?.let { oldPictureUrl ->
            if (oldPictureUrl.isNotEmpty()) {
                try {
                    // Extract the path from the URL
                    val pathRegex = ".+/storage/v1/object/public/$bucketName/(.+)".toRegex()
                    val matchResult = pathRegex.find(oldPictureUrl)
                    val oldPath = matchResult?.groupValues?.get(1)

                    if (!oldPath.isNullOrEmpty()) {
                        Log.d("ProfilePictureUpload", "Deleting old profile picture: $oldPath")
                        supabase.storage.from(bucketName).delete(oldPath)
                        Log.d("ProfilePictureUpload", "Old profile picture deleted successfully")
                    }
                } catch (e: Exception) {
                    // Just log the error but continue with the upload
                    Log.e(
                        "ProfilePictureUpload",
                        "Failed to delete old profile picture: ${e.message}"
                    )
                }
            }
        }

        Log.d("ProfilePictureUpload", "Starting upload for user: $uid")

        // Verify the image URI is valid and accessible
        if (!isUriAccessible(context, imageUri)) {
            Log.e("ProfilePictureUpload", "Image URI is not accessible: $imageUri")
            return null
        }
        val compressedImageBytes = compressImage(context, imageUri)
        Log.d("ProfilePictureUpload", "Image compressed: ${compressedImageBytes.size} bytes")
        val filename = "public/profile_${uid}_v${System.currentTimeMillis()}.jpg"
        Log.d("ProfilePictureUpload", "Attempting to upload to $bucketName/$filename")
        var uploadResult: String? = null
        var attempts = 0
        while (uploadResult == null && attempts < 3) {
            attempts++
            try {
                uploadResult = supabase.storage.from(bucketName)
                    .upload(
                        path = filename,
                        data = compressedImageBytes,
                        upsert = true
                    )
                Log.d(
                    "ProfilePictureUpload",
                    "Upload successful on attempt $attempts: $uploadResult"
                )
            } catch (e: Exception) {
                Log.e("ProfilePictureUpload", "Upload attempt $attempts failed: ${e.message}")
                if (attempts >= 3) throw e
                delay(1000) // Wait before retrying
            }
        }
        val publicUrl = supabase.storage.from(bucketName)
            .publicUrl(filename)
        Log.d("ProfilePictureUpload", "Public URL generated: $publicUrl")
        val updateResult = updateUserProfilePicture(publicUrl)
        Log.d("ProfilePictureUpload", "Database update result: $updateResult")
        publicUrl
    } catch (e: Exception) {
        Log.e("ProfilePictureUpload", "Error uploading profile picture", e)
        null
    }
}

// Helper function to verify URI accessibility
private fun isUriAccessible(context: Context, uri: Uri): Boolean {
    return try {
        context.contentResolver.openInputStream(uri)?.use {
            // If we can open a stream, the URI is accessible
            true
        } == true
    } catch (e: Exception) {
        Log.e("ProfilePictureUpload", "URI accessibility check failed", e)
        false
    }
}

// Helper function to compress and resize image for upload
private fun compressImage(context: Context, imageUri: Uri): ByteArray {
    try {
        // Use InputStreamProvider to properly handle various URI types
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IOException("Failed to open input stream for URI: $imageUri")

        // Use BitmapFactory for more reliable image loading
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        // First pass to get dimensions only
        inputStream.use {
            BitmapFactory.decodeStream(it, null, options)
        }

        // Calculate sample size for downsampling if needed
        val maxWidth = 800
        val maxHeight = 800
        var sampleSize = 1

        if (options.outWidth > maxWidth || options.outHeight > maxHeight) {
            val widthRatio = (options.outWidth.toFloat() / maxWidth.toFloat()).roundToInt()
            val heightRatio = (options.outHeight.toFloat() / maxHeight.toFloat()).roundToInt()
            sampleSize = maxOf(1, minOf(widthRatio, heightRatio))
        }

        // Second pass with actual decoding
        options.apply {
            inJustDecodeBounds = false
            inSampleSize = sampleSize
        }

        // Reopen the stream and decode the bitmap
        val bitmap = context.contentResolver.openInputStream(imageUri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: throw IOException("Failed to decode bitmap from URI: $imageUri")

        // Compress the bitmap
        val outputStream = ByteArrayOutputStream()
        val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        if (!success) {
            Log.w("ProfilePictureUpload", "Bitmap compression may not have been successful")
        }

        return outputStream.toByteArray()
    } catch (e: Exception) {
        Log.e("ProfilePictureUpload", "Error compressing image", e)
        throw e
    }
}

// Updated profile picture update function with more robust error handling
suspend fun updateUserProfilePicture(pictureUrl: String): Boolean {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid ?: return false

    return try {
        val result = supabase.from("users")
            .update({
                set("profile_picture", pictureUrl)
            }) {
                filter {
                    eq("uid", uid)
                }
            }

        // Log the update result for debugging
        Log.d("ProfilePictureUpdate", "Update result: $result")
        true
    } catch (e: Exception) {
        Log.e("ProfilePictureUpdate", "Error updating profile picture", e)
        false
    }
}

// Function to delete user's profile picture
suspend fun deleteProfilePicture(): Boolean {
    val currentUser = FirebaseAuth.getInstance().currentUser
    currentUser?.uid ?: return false

    return try {
        // Get current user to check if they have an existing profile picture
        val user = fetchUser()
        val bucketName = "profile-pictures"

        // Delete the file from storage if it exists
        user?.profile_picture?.let { oldPictureUrl ->
            if (oldPictureUrl.isNotEmpty()) {
                try {
                    // Extract the path from the URL
                    val pathRegex = ".+/storage/v1/object/public/$bucketName/(.+)".toRegex()
                    val matchResult = pathRegex.find(oldPictureUrl)
                    val oldPath = matchResult?.groupValues?.get(1)

                    if (!oldPath.isNullOrEmpty()) {
                        Log.d("ProfilePicture", "Deleting profile picture: $oldPath")
                        supabase.storage.from(bucketName).delete(oldPath)
                    }
                } catch (e: Exception) {
                    Log.e("ProfilePicture", "Failed to delete picture from storage: ${e.message}")
                    // Continue to update database even if storage delete fails
                }
            }
        }

        true
    } catch (e: Exception) {
        Log.e("ProfilePicture", "Error deleting profile picture: ${e.message}")
        false
    }
}