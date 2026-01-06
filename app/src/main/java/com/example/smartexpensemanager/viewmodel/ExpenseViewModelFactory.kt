package com.example.smartexpensemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartexpensemanager.data.ExpenseRepository

/**
 * Factory for creating ExpenseViewModel instances.
 */
class ExpenseViewModelFactory(
    private val repository: ExpenseRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ExpenseViewModel::class.java) ->
                ExpenseViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
