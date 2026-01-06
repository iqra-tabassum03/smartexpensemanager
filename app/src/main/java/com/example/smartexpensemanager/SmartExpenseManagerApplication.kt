package com.example.smartexpensemanager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.smartexpensemanager.data.TransactionRepository
import com.example.smartexpensemanager.data.ExpenseRepository

class SmartExpenseManagerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        // ✅ Create the notification channel for daily reminders
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "expense_reminder_channel", // Channel ID
                "Expense Reminders",        // Channel Name (shown in settings)
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminder to add your expenses"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
