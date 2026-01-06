package com.example.smartexpensemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a financial transaction (income or expense).
 * Stored in the "transactions" table.
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Default to userId = 1 unless you plan multi-user support
    val userId: Int = 1,

    val title: String,        // Transaction title (e.g., Salary, Coffee)
    val amount: Double,       // Transaction amount
    val type: String,         // "Income" or "Expense"
    val category: String,     // Category (Food, Rent, Salary, etc.)

    // Default to current time so filters (Last 7 Days, This Month, This Year) work
    val timestamp: Long = System.currentTimeMillis()
)
