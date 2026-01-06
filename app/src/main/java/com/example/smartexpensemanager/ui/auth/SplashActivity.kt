package com.example.smartexpensemanager.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexpensemanager.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            //  User already logged in → go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            //  Not logged in → go to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish() // Close splash so user can’t go back here
    }
}
