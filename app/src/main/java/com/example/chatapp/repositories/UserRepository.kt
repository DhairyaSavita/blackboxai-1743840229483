package com.example.chatapp.repositories

import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getCurrentUser(): User? {
        return auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).get().await().toObject<User>()
        }
    }

    suspend fun getUserById(userId: String): User? {
        return db.collection("users").document(userId).get().await().toObject<User>()
    }

    suspend fun updateUserProfile(user: User) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).set(user).await()
        }
    }

    suspend fun searchUsers(query: String): List<User> {
        val result = db.collection("users")
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff")
            .get()
            .await()
        
        return result.documents.mapNotNull { it.toObject<User>() }
    }
}