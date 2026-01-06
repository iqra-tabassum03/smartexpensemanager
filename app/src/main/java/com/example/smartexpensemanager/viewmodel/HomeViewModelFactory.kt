package com.example.smartexpensemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartexpensemanager.data.TransactionRepository

/**
 * Factory for creating a HomeViewModel with a TransactionRepository dependency.
 * This is necessary because ViewModels with custom constructors require a Factory.
 */
class HomeViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}