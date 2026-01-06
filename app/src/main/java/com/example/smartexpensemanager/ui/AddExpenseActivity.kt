package com.example.smartexpensemanager.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.data.ExpenseRepository
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private val userId = 1
    private lateinit var repository: ExpenseRepository

    // View references
    private lateinit var etAmount: EditText
    private lateinit var etDescription: EditText
    private lateinit var spCategory: MaterialAutoCompleteTextView
    private lateinit var toggleType: MaterialButtonToggleGroup
    private lateinit var btnSave: Button
    private lateinit var tvTitle: TextView
    private lateinit var etDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Initialize repository using singleton
        repository = ExpenseRepository.getInstance(applicationContext)

        // Initialize views
        etAmount = findViewById(R.id.et_amount)
        etDescription = findViewById(R.id.et_description)
        spCategory = findViewById(R.id.sp_category)
        toggleType = findViewById(R.id.toggle_type)
        btnSave = findViewById(R.id.btn_save_transaction)
        tvTitle = findViewById(R.id.tv_title)
        etDate = findViewById(R.id.et_date)

        setupCategoryDropdown()
        setupDateInput()

        tvTitle.text = "Add New Transaction"

        btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun setupCategoryDropdown() {
        val categories = listOf(
            "Food", "Transport", "Study", "Entertainment",
            "Bills", "Salary", "Other Income"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        spCategory.setAdapter(adapter)
    }

    private fun setupDateInput() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etDate.setText(dateFormat.format(Date()))
        // TODO: hook up a DatePickerDialog here
    }

    private fun saveTransaction() {
        val amountStr = etAmount.text.toString()
        val description = etDescription.text.toString().trim()
        val category = spCategory.text.toString()
        val isIncome = toggleType.checkedButtonId == R.id.btn_income

        // Input validation
        if (amountStr.isEmpty() || description.isEmpty()) {
            Snackbar.make(btnSave, "Please fill in all required fields (Amount and Description).", Snackbar.LENGTH_LONG).show()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Snackbar.make(btnSave, "Please enter a valid amount.", Snackbar.LENGTH_LONG).show()
            return
        }

        val dateString = etDate.text.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date? = try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            Date()
        }

        if (date == null) {
            Snackbar.make(btnSave, "Invalid date format.", Snackbar.LENGTH_LONG).show()
            return
        }

        val newExpense = com.example.smartexpensemanager.data.model.Expense(
            id = 0,
            userId = userId,
            title = description,
            amount = amount,
            type = if (isIncome) "Income" else "Expense",
            category = category,
            timestamp = date.time
        )

        lifecycleScope.launch {
            repository.insertExpense(newExpense)
            Snackbar.make(btnSave, "${if (isIncome) "Income" else "Expense"} Saved!", Snackbar.LENGTH_LONG).show()
            finish()
        }
    }
}
