package com.example.smartexpensemanager.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, 0, openAppIntent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        // ✅ Funny but professional rotating messages
        val messages = listOf(
            "💸 Hey big spender! Don’t forget to log today’s expenses before they vanish into thin air.",
            "📊 Your wallet told me it wants transparency. Add today’s expenses and keep your budget honest!",
            "🕒 Daily check‑in time! Record your expenses now — future you will thank present you.",
            "🧾 Numbers don’t lie, but they do slip away. Capture today’s spending before it escapes!",
            "💼 Professional tip: budgets are built daily. Add your expenses now and stay ahead of the curve."
        )

        val randomMessage = messages.random()

        val notification = NotificationCompat.Builder(context, "expense_reminder_channel")
            .setSmallIcon(R.mipmap.ic_launcher) // ✅ use mipmap, not drawable
            .setContentTitle("Smart Expense Manager")
            .setContentText(randomMessage)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // ✅ Safe notify call (Android 13+ requires POST_NOTIFICATIONS permission)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(1001, notification)
        }
    }
}
