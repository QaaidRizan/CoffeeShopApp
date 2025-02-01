package com.example.coffeeshop.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.coffeeshop.databinding.ActivityUsersingupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : BaseActivity() {

    private val binding: ActivityUsersingupBinding by lazy {
        ActivityUsersingupBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth // Firebase Authentication instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth

        // Handle Sign Up button click
        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString().trim()
            val password = binding.signupPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUpUser(email, password)
        }

        // Handle "Already have an account?" text click
        binding.alreadyAccountText.setOnClickListener {
            finish() // Close SignupActivity and return to the login screen
        }
    }

    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign-up Successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // Close SignupActivity
                } else {
                    Toast.makeText(this, "Sign-up Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
