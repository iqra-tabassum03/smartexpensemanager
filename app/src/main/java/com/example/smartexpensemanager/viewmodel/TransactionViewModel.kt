package com.example.smartexpensemanager.viewmodel

import androidx.lifecycle.*
import com.example.smartexpensemanager.data.TransactionRepository
import com.example.smartexpensemanager.data.model.Transaction
import com.example.smartexpensemanager.data.model.CategoryTotal
import com.example.smartexpensemanager.data.model.DailyTotal
import com.example.smartexpensemanager.data.model.MonthlyTotal
import com.example.smartexpensemanager.data.model.YearlyTotal
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    fun getAllTransactions(userId: Int): LiveData<List<Transaction>> =
        repository.getAllTransactions(userId).asLiveData()

    fun getRecentTransactions(userId: Int): LiveData<List<Transaction>> =
        repository.getAllTransactions(userId)
            .map { list -> list.sortedByDescending { it.timestamp }.take(5) }
            .asLiveData()

    fun saveTransaction(
        userId: Int,
        title: String,
        amount: Double,
        type: String,
        category: String,
        timestamp: Long
    ) {
        viewModelScope.launch {
            val newTransaction = Transaction(
                id = 0,
                userId = userId,
                title = title,
                amount = amount,
                type = type,
                category = category,
                timestamp = timestamp
            )
            repository.insert(newTransaction)
        }
    }

    fun getTransactionsByType(userId: Int, type: String): LiveData<List<Transaction>> =
        repository.getAllTransactions(userId)
            .map { list -> list.filter { it.type.equals(type, ignoreCase = true) } }
            .asLiveData()

    fun getTransactionsByCategory(userId: Int, category: String): LiveData<List<Transaction>> =
        repository.getAllTransactions(userId)
            .map { list -> list.filter { it.category.equals(category, ignoreCase = true) } }
            .asLiveData()

    fun getCategoryTotals(userId: Int): LiveData<List<CategoryTotal>> =
        repository.getCategoryTotals(userId).asLiveData()

    fun getDailyTotals(userId: Int): LiveData<List<DailyTotal>> =
        repository.getDailyTotals(userId).asLiveData()

    fun getMonthlyTotals(userId: Int): LiveData<List<MonthlyTotal>> =
        repository.getMonthlyTotals(userId).asLiveData()

    fun getYearlyTotals(userId: Int): LiveData<List<YearlyTotal>> =
        repository.getYearlyTotals(userId).asLiveData()
}
