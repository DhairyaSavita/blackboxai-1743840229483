package com.example.chatapp.repositories

import com.example.chatapp.models.Chat
import com.example.chatapp.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun createChat(participantIds: List<String>): String {
        val chat = Chat(
            participants = participantIds,
            lastMessage = "",
            lastMessageTime = Date().time
        )
        
        val docRef = db.collection("chats").add(chat).await()
        return docRef.id
    }

    suspend fun getChatsForUser(): List<Chat> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val result = db.collection("chats")
            .whereArrayContains("participants", userId)
            .get()
            .await()
        
        return result.documents.mapNotNull { 
            it.toObject<Chat>()?.copy(id = it.id) 
        }
    }

    suspend fun sendMessage(chatId: String, text: String) {
        val message = Message(
            senderId = auth.currentUser?.uid ?: "",
            text = text,
            timestamp = Date().time
        )
        
        // Add message to subcollection
        db.collection("chats").document(chatId)
            .collection("messages")
            .add(message)
            .await()
        
        // Update last message in chat
        db.collection("chats").document(chatId)
            .update(
                "lastMessage", text,
                "lastMessageTime", message.timestamp
            )
            .await()
    }

    suspend fun getMessages(chatId: String): List<Message> {
        val result = db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .await()
        
        return result.documents.mapNotNull {
            it.toObject<Message>()?.copy(id = it.id)
        }
    }
}