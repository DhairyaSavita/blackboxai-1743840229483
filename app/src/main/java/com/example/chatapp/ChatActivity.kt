package com.example.chatapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var messageListener: ListenerRegistration
    private var chatId: String? = null
    private var recipientId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        chatId = intent.getStringExtra("chatId")
        recipientId = intent.getStringExtra("recipientId")

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        // TODO: Set up adapter for messages
    }

    private fun setupListeners() {
        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding.messageEditText.text?.clear()
            }
        }
    }

    private fun sendMessage(message: String) {
        chatId?.let { id ->
            val messageData = hashMapOf(
                "senderId" to auth.currentUser?.uid,
                "text" to message,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("chats").document(id)
                .collection("messages")
                .add(messageData)
                .addOnFailureListener { e ->
                    // TODO: Handle error
                }
        }
    }

    override fun onStart() {
        super.onStart()
        chatId?.let { id ->
            messageListener = db.collection("chats").document(id)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    // TODO: Handle message updates
                }
        }
    }

    override fun onStop() {
        super.onStop()
        messageListener.remove()
    }
}