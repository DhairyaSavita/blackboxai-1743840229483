package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.chatapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : BaseActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if (validateInputs(name, email, password, confirmPassword)) {
                signupUser(name, email, password)
            }
        }

        binding.loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun validateInputs(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isEmpty()) {
            binding.nameEditText.error = "Name cannot be empty"
            return false
        }
        if (email.isEmpty()) {
            binding.emailEditText.error = getString(R.string.error_email)
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            binding.passwordEditText.error = getString(R.string.error_password)
            return false
        }
        if (password != confirmPassword) {
            binding.confirmPasswordEditText.error = getString(R.string.error_password_match)
            return false
        }
        return true
    }

    private fun signupUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserToFirestore(name, email)
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.error_general),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveUserToFirestore(name: String, email: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email
        )

        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId)
                .set(user)
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to save user data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}