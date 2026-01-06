package com.example.smartexpensemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensemanager.data.TransactionRepository
import com.example.smartexpensemanager.data.model.Transaction
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class HomeViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _currentBalance = MutableLiveData<Double>()
    val currentBalance: LiveData<Double> = _currentBalance

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpenses = MutableLiveData<Double>()
    val totalExpenses: LiveData<Double> = _totalExpenses

    private val userId = 1 // TODO: replace with actual logged-in user id

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        viewModelScope.launch {
            repository.getAllTransactions(userId).collect { transactionsList ->
                _transactions.value = transactionsList

                val income = transactionsList
                    .filter { it.type.equals("Income", ignoreCase = true) }
                    .sumOf { it.amount }

                val expenses = transactionsList
                    .filter { it.type.equals("Expense", ignoreCase = true) }
                    .sumOf { it.amount }

                val balance = income - expenses

                _totalIncome.value = income
                _totalExpenses.value = expenses
                _currentBalance.value = balance
            }
        }
    }
}
