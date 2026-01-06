package com.example.smartexpensemanager

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.smartexpensemanager.data.model.Transaction
import com.example.smartexpensemanager.databinding.ActivityMainBinding
import com.example.smartexpensemanager.ui.auth.LoginActivity
import com.example.smartexpensemanager.ui.notifications.ReminderReceiver
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val email = prefs.getString("user_email", null)
        val password = prefs.getString("user_password", null)

        if (email == null || password == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        binding.fabAddTransaction.setOnClickListener { showAddTransactionDialog() }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        scheduleDailyReminder()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun scheduleDailyReminder() {
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val hour = prefs.getInt("reminder_hour", 21)
        val minute = prefs.getInt("reminder_minute", 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun showFab() = binding.fabAddTransaction.show()
    fun hideFab() = binding.fabAddTransaction.hide()

    private fun showAddTransactionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_transaction, null)

        val etDescription = dialogView.findViewById<EditText>(R.id.et_description)
        val etAmount = dialogView.findViewById<EditText>(R.id.et_amount)
        val spinnerCategory = dialogView.findViewById<AutoCompleteTextView>(R.id.spinner_category)
        val etDate = dialogView.findViewById<EditText>(R.id.et_date)
        val toggleGroup = dialogView.findViewById<MaterialButtonToggleGroup>(R.id.type_toggle_group)

        val categories = listOf("Food", "Rent", "Salary", "Transport", "Shopping", "Other")
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        spinnerCategory.setAdapter(adapter)

        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this,
                { _, year, month, day ->
                    val pickedCal = Calendar.getInstance()
                    pickedCal.set(year, month, day)
                    etDate.setText("$day/${month + 1}/$year")
                    etDate.tag = pickedCal.timeInMillis
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        dialogView.findViewById<View>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<View>(R.id.btn_save).setOnClickListener {
            val title = etDescription.text.toString().trim()
            val amount = etAmount.text.toString().toDoubleOrNull()
            val category = spinnerCategory.text.toString().trim()
            val timestamp = etDate.tag as? Long ?: System.currentTimeMillis()

            val type = when (toggleGroup.checkedButtonId) {
                R.id.toggle_income -> "Income"
                R.id.toggle_expense -> "Expense"
                else -> "Expense"
            }

            if (title.isEmpty()) {
                etDescription.error = "Title required"
                return@setOnClickListener
            }
            if (amount == null || amount <= 0) {
                etAmount.error = "Enter valid amount"
                return@setOnClickListener
            }
            if (category.isEmpty()) {
                spinnerCategory.error = "Select category"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val tx = Transaction(
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    timestamp = timestamp
                )
                (application as SmartExpenseManagerApplication)
                    .container.transactionRepository.insert(tx)
            }
            dialog.dismiss()
        }

        dialog.show()
    }
}
