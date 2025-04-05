package com.example.chatapp.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImage: String = "",
    val lastSeen: Long = 0L
)