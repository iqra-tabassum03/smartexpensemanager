package com.example.smartexpensemanager.viewmodel

import androidx.lifecycle.*
import com.example.smartexpensemanager.data.model.Expense
import com.example.smartexpensemanager.data.ExpenseRepository
import com.example.smartexpensemanager.data.model.CategoryTotal
import com.example.smartexpensemanager.data.model.DailyTotal
import com.example.smartexpensemanager.data.model.MonthlyTotal
import com.example.smartexpensemanager.data.model.YearlyTotal
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling expenses:
 * - Adding new ones
 * - Exposing all expenses for HomeFragment
 * - Providing aggregated totals for ReportsFragment
 * - Deleting expenses
 */
class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // All expenses for a given user
    fun getAllExpenses(userId: Int): LiveData<List<Expense>> =
        repository.getAllExpenses(userId).asLiveData()

    // Recent transactions (last 5)
    fun getRecentTransactions(userId: Int): LiveData<List<Expense>> =
        repository.getRecentTransactions(userId).asLiveData()

    // ✅ Reactive totals for HomeFragment panel
    fun getTotalIncome(userId: Int): LiveData<Double> =
        repository.getTotalIncome(userId).asLiveData()

    fun getTotalExpenses(userId: Int): LiveData<Double> =
        repository.getTotalExpenses(userId).asLiveData()

    // Insert a new expense
    fun saveExpense(
        title: String,
        amount: Double,
        type: String,
        category: String,
        timestamp: Long,
        userId: Int
    ) {
        viewModelScope.launch {
            val newExpense = Expense(
                id = 0, // Room auto-generates
                userId = userId,
                title = title,
                amount = amount,
                type = type,
                category = category,
                timestamp = timestamp
            )
            repository.insertExpense(newExpense)
        }
    }

    // ✅ Delete an expense by passing the object
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // ✅ Delete an expense by ID
    fun deleteExpenseById(id: Int) {
        viewModelScope.launch {
            repository.deleteExpenseById(id)
        }
    }

    // Aggregated totals for reports
    fun getCategoryTotals(userId: Int): LiveData<List<CategoryTotal>> =
        repository.getCategoryTotals(userId).asLiveData()

    fun getDailyTotals(userId: Int): LiveData<List<DailyTotal>> =
        repository.getDailyTotals(userId).asLiveData()

    fun getMonthlyTotals(userId: Int): LiveData<List<MonthlyTotal>> =
        repository.getMonthlyTotals(userId).asLiveData()

    fun getYearlyTotals(userId: Int): LiveData<List<YearlyTotal>> =
        repository.getYearlyTotals(userId).asLiveData()
}
