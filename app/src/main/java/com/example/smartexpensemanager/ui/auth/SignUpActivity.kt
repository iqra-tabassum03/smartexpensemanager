package com.example.smartexpensemanager.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexpensemanager.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text?.toString()?.trim().orEmpty()
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val password = binding.etPassword.text?.toString()?.trim().orEmpty()
            val confirm = binding.etConfirmPassword.text?.toString()?.trim().orEmpty()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("user_name", name)
                .putString("user_email", email)
                .putString("user_password", password)
                .apply()

            Toast.makeText(this, "Account created! Please login.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
