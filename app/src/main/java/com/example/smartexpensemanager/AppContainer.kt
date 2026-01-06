package com.example.smartexpensemanager

import android.content.Context
import com.example.smartexpensemanager.data.TransactionRepository
import com.example.smartexpensemanager.data.ExpenseRepository

/**
 * AppContainer defines dependencies available across the app.
 */
interface AppContainer {
    val transactionRepository: TransactionRepository
    val expenseRepository: ExpenseRepository
}

/**
 * AppDataContainer provides concrete implementations of repositories.
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepository.getInstance(context)
    }

    override val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepository.getInstance(context)
    }
}
