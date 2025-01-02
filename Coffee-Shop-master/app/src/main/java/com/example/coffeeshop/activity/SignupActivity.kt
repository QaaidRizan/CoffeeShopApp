package com.example.coffeeshop.activity

import android.os.Bundle

import com.example.coffeeshop.databinding.ActivityUsersingupBinding

class SignupActivity : BaseActivity() {

    // View Binding for the SignupActivity
    private val binding: ActivityUsersingupBinding by lazy {
        ActivityUsersingupBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // Set the content view using View Binding

        // Handle the Sign Up button click
        binding.signupButton.setOnClickListener {
            // Add logic for signup action here
            // Example: Show a Toast or navigate to another activity
            // Toast.makeText(this, "Signup Clicked", Toast.LENGTH_SHORT).show()
        }

        // Handle the "Already have an account?" text click
        binding.alreadyAccountText.setOnClickListener {
            // Navigate back to the LoginActivity
            finish() // Close SignupActivity and return to the previous screen
        }
    }
}
