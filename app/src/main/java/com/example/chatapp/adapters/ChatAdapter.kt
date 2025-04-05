package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemChatBinding
import com.example.chatapp.models.Chat
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val chats: List<Chat>,
    private val onChatClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemChatBinding.bind(itemView)
        
        fun bind(chat: Chat) {
            // Get the other participant's name (for 1:1 chats)
            val otherParticipant = chat.participants.firstOrNull { it != auth.currentUser?.uid }
            binding.chatName.text = otherParticipant ?: "Group Chat"
            
            binding.lastMessage.text = chat.lastMessage
            binding.lastMessageTime.text = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                .format(Date(chat.lastMessageTime))
            
            itemView.setOnClickListener { onChatClick(chat) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount() = chats.size
}