package com.example.smartexpensemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartexpensemanager.data.TransactionRepository

class ReportsViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
