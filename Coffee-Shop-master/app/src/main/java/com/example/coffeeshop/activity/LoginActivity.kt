package com.example.coffeeshop.activity

import android.content.Intent
import android.os.Bundle
import com.example.coffeeshop.databinding.ActivityUserdetailsBinding

class LoginActivity : BaseActivity() {

    // View Binding for LoginActivity
    private val binding: ActivityUserdetailsBinding by lazy {
        ActivityUserdetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // Set content view using View Binding

        // Handle Login Button Click
        binding.loginButton.setOnClickListener {

            startActivity(Intent(this, MainActivity::class.java))
            // Add logic to validate credentials or navigate further
            // Example: Show a Toast
            // Toast.makeText(this, "Login Clicked", Toast.LENGTH_SHORT).show()
        }

        // Handle "Sign Up" Text Click
        binding.signupText.setOnClickListener {
            // Navigate to SignupActivity
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}