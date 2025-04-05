package com.example.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.adapters.UserAdapter
import com.example.chatapp.databinding.ActivityNewChatBinding
import com.example.chatapp.models.User
import com.example.chatapp.repositories.ChatRepository
import com.example.chatapp.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewChatBinding
    private val userRepository = UserRepository()
    private val chatRepository = ChatRepository()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadUsers()
    }

    private fun setupRecyclerView() {
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.adapter = UserAdapter(emptyList()) { user ->
            createChatWithUser(user)
        }
    }

    private fun loadUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = userRepository.searchUsers("")
                withContext(Dispatchers.Main) {
                    (binding.usersRecyclerView.adapter as? UserAdapter)?.updateUsers(
                        users.filter { it.id != currentUserId }
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun createChatWithUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val chatId = chatRepository.createChat(listOf(currentUserId, user.id))
                withContext(Dispatchers.Main) {
                    setResult(RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}