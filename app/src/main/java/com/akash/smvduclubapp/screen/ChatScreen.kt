@file:OptIn(SupabaseExperimental::class, SupabaseInternal::class)
package com.akash.smvduclubapp.screen

import android.R.attr.password
import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.ChatMessage
import com.akash.smvduclubapp.data.ChatRoom
import com.akash.smvduclubapp.data.EventDescription
import com.akash.smvduclubapp.data.User
import com.akash.smvduclubapp.data.fetchAndListenForMessages
import com.akash.smvduclubapp.data.fetchUser
import com.akash.smvduclubapp.data.leaveChat
import com.akash.smvduclubapp.data.sendMessage
import com.akash.smvduclubapp.database.supabase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.compareTo
import kotlin.text.format
import kotlin.text.get
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    roomId: String,
    userName: String,
    onChatRoomLoaded: (ChatRoom?) -> Unit = {},
) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser = remember { firebaseAuth.currentUser }
    val currentUserId = remember { firebaseUser?.uid ?: "" }
    var chatRoom by remember { mutableStateOf<ChatRoom?>(null) }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var isLeaving by remember { mutableStateOf(false) }
    var joinMessageSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val sharedPrefs = remember {
        context.getSharedPreferences("chat_prefs", android.content.Context.MODE_PRIVATE)
    }

    // Handle keyboard insets
    val windowInsets = WindowInsets.ime
        .only(WindowInsetsSides.Bottom)
        .union(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))

    DisposableEffect(roomId) {
        val db = FirebaseFirestore.getInstance()
        val messageListenerRegistration: ListenerRegistration?
        val roomListenerRegistration: ListenerRegistration?

        // Listener setup
        roomListenerRegistration = db.collection("chatrooms").document(roomId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatScreen", "Error loading chat room: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val newChatRoom = snapshot.toObject(ChatRoom::class.java)
                    chatRoom = newChatRoom
                    onChatRoomLoaded(chatRoom)

                    if (!joinMessageSent && newChatRoom != null) {
                        joinMessageSent = true
                        scope.launch {
                            sendMessage(
                                roomId = roomId,
                                userId = "system",
                                userName = "System",
                                content = "$userName has joined the chat"
                            )
                        }
                    }
                }
            }

        messageListenerRegistration = db.collection("messages")
            .whereEqualTo("roomId", roomId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatScreen", "Error listening for messages: ${error.message}")
                    isLoading = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val updatedMessages = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(ChatMessage::class.java)?.copy(messageId = doc.id)
                        } catch (e: Exception) {
                            Log.e("ChatScreen", "Error converting doc ${doc.id}", e)
                            null
                        }
                    }

                    messages = updatedMessages
                    isLoading = false

                    if (messages.isNotEmpty()) {
                        scope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                }
            }

        onDispose {
            messageListenerRegistration?.remove()
            roomListenerRegistration?.remove()
        }
    }


    LaunchedEffect(currentUserId) {
        if (currentUserId.isEmpty()) {
            navController.popBackStack()
        }
    }
    LaunchedEffect(chatRoom) {
        onChatRoomLoaded(chatRoom)
    }

    fun handleLeaveChat() {
        if (isLeaving || chatRoom == null) return

        isLeaving = true
        scope.launch {
            try {
                // Send a message to notify others that user has left
                sendMessage(
                    roomId = roomId,
                    userId = "system",  // Use "system" as user ID for system messages
                    userName = "System",
                    content = "$userName has left the chat"
                )
                // Wait a moment to ensure message is sent before leaving
                kotlinx.coroutines.delay(500)

                // Now leave the chat
                leaveChat(
                    userId = currentUserId,
                    roomId = roomId,
                    clubId = chatRoom?.clubId ?: ""
                )
                navController.popBackStack()
            } catch (e: Exception) {
                isLeaving = false
            }
        }
    }

    Scaffold(
        contentWindowInsets = windowInsets,
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type a message...") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_chat_24),
                                contentDescription = "Chat",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        },
                        singleLine = false,
                        maxLines = 4
                    )

                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                val messageToSend = messageText
                                messageText = ""

                                val tempMessage = ChatMessage(
                                    messageId = UUID.randomUUID().toString(),
                                    roomId = roomId,
                                    senderId = currentUserId,
                                    senderName = userName,
                                    content = messageToSend,
                                    timestamp = Timestamp.now(),
                                    isLocal = true // Mark as local until confirmed by Firebase
                                )
                                messages = messages + tempMessage

                                // Scroll to bottom immediately
                                scope.launch {
                                    listState.animateScrollToItem(messages.size - 1)
                                }

                                scope.launch {
                                    try {
                                        sendMessage(roomId, currentUserId, userName, messageToSend)
                                    } catch (e: Exception) {
                                        Log.e("ChatScreen", "Error sending message", e)
                                        // Optionally show error to user
                                        messages = messages.filter { it.messageId != tempMessage.messageId }
                                        messageText = messageToSend // Restore message if sending failed
                                    }
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                val groupedMessages = messages.groupBy {
                    val date = it.timestamp.toDate()
                    "${date.year}-${date.month}-${date.date}"
                }

                groupedMessages.forEach { (dateKey, messagesForDate) ->
                    val firstMsg = messagesForDate.firstOrNull() ?: return@forEach
                    val dateHeader = formatDateHeader(firstMsg.timestamp)

                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dateHeader,
                                color = Color.LightGray,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                            )
                        }
                    }

                    items(messagesForDate) { message ->
                        val isCurrentUser = message.senderId == currentUserId
                        MessageItem(message, isCurrentUser)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            ) {
                IconButton(
                    onClick = { handleLeaveChat() },
                    enabled = !isLeaving
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_exit),
                        contentDescription = "Leave Chat",
                        tint = if (isLeaving) Color.Gray else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
@Composable
fun MessageItem(message: ChatMessage, isCurrentUser: Boolean) {
    val isSystemMessage = message.senderId == "system"

    if (isSystemMessage) {
        // Display system message centered with different styling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message.content,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    } else{
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
            ) {
                if (!isCurrentUser) {
                    // Show profile image for other user
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF9C9C9C))
                            .align(Alignment.Top),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "UserAvatar",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column {
                    if (!isCurrentUser) {
                        Text(
                            text = message.senderName,
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                        )
                    }

                    Card(
                        modifier = Modifier
                            .widthIn(max = 260.dp)
                            .padding(bottom = 2.dp),
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isCurrentUser) 18.dp else 6.dp,
                            bottomEnd = if (isCurrentUser) 6.dp else 18.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentUser)
                                MaterialTheme.colorScheme.primary
                            else
                                Color(0xFF3B3B3B) // Dark gray for received messages
                        )
                    ) {
                        Text(
                            text = message.content,
                            color = if (isCurrentUser) Color.White else Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Text(
                        text = formatTime(message.timestamp),
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}



fun formatTime(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val hours = if (date.hours > 12) (date.hours - 12) else date.hours
    val amPm = if (date.hours >= 12) "PM" else "AM"
    val hoursStr = if (hours == 0) "12" else hours.toString()
    val minutes = date.minutes.toString().padStart(2, '0')
    return "$hoursStr:$minutes $amPm"
}



fun formatDateHeader(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()
    calendar.time = date

    return when {
        calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) ->
            "Today ${formatTime(timestamp)}"

        calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1 ->
            "Yesterday ${formatTime(timestamp)}"

        else -> {
            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            dateFormat.format(date)
        }
    }
}

