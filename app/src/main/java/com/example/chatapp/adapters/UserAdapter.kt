package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemUserBinding
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth

class UserAdapter(
    private var users: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUserBinding.bind(itemView)
        
        fun bind(user: User) {
            binding.userName.text = user.name
            binding.userEmail.text = user.email
            
            // Hide current user from the list
            if (user.id == currentUserId) {
                itemView.visibility = View.GONE
                itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            } else {
                itemView.visibility = View.VISIBLE
                itemView.layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                itemView.setOnClickListener { onUserClick(user) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers.filter { it.id != currentUserId }
        notifyDataSetChanged()
    }
}