@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)
package com.akash.smvduclubapp.data

import android.util.Log
import com.akash.smvduclubapp.database.supabase
import com.akash.smvduclubapp.notification.FCMService
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.tasks.await
import kotlin.text.get

data class ChatRoom(
    @DocumentId
    val roomId: String = "",
    val clubId: String = "",
    val clubName: String = "",
    val clubLogo: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val memberIds: List<String> = emptyList(),
)

data class ChatMessage(
    @DocumentId
    val messageId: String = "",
    val roomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val isLocal: Boolean = false
)

suspend fun createOrJoinChatRoom(club: Club, userId: String, userName: String): ChatRoom {
    val db = FirebaseFirestore.getInstance()
    val chatRoomsRef = db.collection("chatrooms")

    // Check if a chatroom for this club already exists
    val existingRoomQuery = chatRoomsRef.whereEqualTo("clubId", club.clubId).get().await()

    if (!existingRoomQuery.isEmpty) {
        // Chatroom exists, add user to it
        val roomDoc = existingRoomQuery.documents[0]
        val chatRoom = roomDoc.toObject(ChatRoom::class.java)!!

        // Add user if not already a member
        if (!chatRoom.memberIds.contains(userId)) {
            val updatedMembers = chatRoom.memberIds + userId
            roomDoc.reference.update("memberIds", updatedMembers).await()
            return chatRoom.copy(memberIds = updatedMembers)
        }
        return chatRoom
    } else {
        // Create new chatroom
        val newChatRoom = ChatRoom(
            clubId = club.clubId,
            clubName = club.name,
            clubLogo = club.club_logo,
            memberIds = listOf(userId)
        )

        val docRef = chatRoomsRef.add(newChatRoom).await()
        return newChatRoom.copy(roomId = docRef.id)
    }
}

suspend fun sendMessage(roomId: String, userId: String, userName: String, content: String) {
    try {
        val db = FirebaseFirestore.getInstance()
        val message = ChatMessage(
            roomId = roomId,
            senderId = userId,
            senderName = userName,
            content = content,
            timestamp = Timestamp.now()
        )

        // Add message to Firestore
        db.collection("messages").add(message).await()

        // Log success
        Log.d("ChatRoom", "Message sent successfully to room: $roomId")
    } catch (e: Exception) {
        Log.e("ChatRoom", "Error sending message: ${e.message}", e)
        throw e
    }
}

suspend fun leaveChat(userId: String, roomId: String, clubId: String) {
    try {
        // 1. Get Firebase reference
        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chatrooms").document(roomId)

        // 2. Get current room data
        val roomSnapshot = chatRoomRef.get().await()
        val chatRoom = roomSnapshot.toObject(ChatRoom::class.java)

        // 3. Remove user from memberIds list
        chatRoom?.let {
            val updatedMembers = it.memberIds.filter { memberId -> memberId != userId }
            chatRoomRef.update("memberIds", updatedMembers).await()
        }

        // 4. Delete user-club relation from Supabase
        supabase.from("user_club_relation")
            .delete {
                filter {
                    eq("user_id", userId)
                    eq("club_id", clubId)
                }
            }
    } catch (e: Exception) {
        Log.e("ChatRoom", "Error leaving chat: ${e.message}")
        throw e
    }

}
fun fetchAndListenForMessages(
    roomId: String,
    onNewMessages: (List<ChatMessage>) -> Unit,
    onError: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    Log.d("ChatRoom", "Starting message listener for room: $roomId")

    // Create a query that will get all messages for this room
    val query = db.collection("messages")
        .whereEqualTo("roomId", roomId)
        .orderBy("timestamp", Query.Direction.ASCENDING)

    // Register the snapshot listener
    query.addSnapshotListener { snapshot, error ->
        if (error != null) {
            Log.e("ChatRoom", "Error fetching messages: ${error.message}", error)
            onError(error)
            return@addSnapshotListener
        }

        if (snapshot != null) {
            try {
                val messages = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(ChatMessage::class.java)?.copy(messageId = doc.id)
                    } catch (e: Exception) {
                        Log.e("ChatRoom", "Error parsing message document ${doc.id}: ${e.message}", e)
                        null
                    }
                }

                Log.d("ChatRoom", "Successfully fetched ${messages.size} messages for room $roomId")
                onNewMessages(messages)
            } catch (e: Exception) {
                Log.e("ChatRoom", "Error processing messages: ${e.message}", e)
                onError(e)
            }
        } else {
            Log.w("ChatRoom", "Received null snapshot for room $roomId")
            onNewMessages(emptyList())
        }
    }
}