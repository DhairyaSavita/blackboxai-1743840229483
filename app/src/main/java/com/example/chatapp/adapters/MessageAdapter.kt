package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemMessageBinding
import com.example.chatapp.models.Message
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val messages: List<Message>) : 
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemMessageBinding.bind(itemView)
        
        fun bind(message: Message) {
            binding.messageText.text = message.text
            binding.messageTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(Date(message.timestamp))
            
            // Set different background for sent vs received messages
            val isCurrentUser = message.senderId == auth.currentUser?.uid
            val backgroundRes = if (isCurrentUser) R.color.message_sent else R.color.message_received
            binding.root.setBackgroundResource(backgroundRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size
}