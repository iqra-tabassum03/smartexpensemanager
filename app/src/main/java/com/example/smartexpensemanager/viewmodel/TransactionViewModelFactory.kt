package com.example.smartexpensemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartexpensemanager.data.TransactionRepository

/**
 * Factory for creating TransactionViewModel instances.
 */
class TransactionViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            TransactionViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
