package com.example.smartexpensemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,          // description/title of the expense
    val amount: Double,
    val type: String,           // "Income" or "Expense"
    val category: String,
    val timestamp: Long         // store date as epoch millis
)
