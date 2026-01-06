package com.example.smartexpensemanager.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexpensemanager.MainActivity
import com.example.smartexpensemanager.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPrimaryAction.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val password = binding.etPassword.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val savedEmail = prefs.getString("user_email", null)
            val savedPassword = prefs.getString("user_password", null)

            if (email == savedEmail && password == savedPassword) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                // ✅ Save login state
                prefs.edit().putBoolean("is_logged_in", true).apply()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSecondaryAction.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}
