package com.example.smartexpensemanager.viewmodel

import androidx.lifecycle.*
import com.example.smartexpensemanager.data.TransactionRepository
import com.example.smartexpensemanager.data.model.CategoryTotal
import com.example.smartexpensemanager.data.model.DailyTotal
import com.example.smartexpensemanager.data.model.MonthlyTotal
import com.example.smartexpensemanager.data.model.YearlyTotal
import com.example.smartexpensemanager.data.model.Transaction
import kotlinx.coroutines.launch

class ReportsViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _filter = MutableLiveData("This Month")
    val filter: LiveData<String> = _filter

    private val userId = 1

    // ✅ Category totals switch based on filter (DAO-driven)
    val categoryTotals: LiveData<List<CategoryTotal>> = _filter.switchMap { f ->
        when (f) {
            "Last 7 Days" -> repository.getCategoryTotalsLast7Days(userId).asLiveData()
            "This Month"  -> repository.getCategoryTotalsThisMonth(userId).asLiveData()
            "This Year"   -> repository.getCategoryTotalsThisYear(userId).asLiveData()
            else          -> repository.getCategoryTotals(userId).asLiveData()
        }
    }

    // ✅ Daily totals switch based on filter (DAO-driven)
    val dailyTotals: LiveData<List<DailyTotal>> = _filter.switchMap { f ->
        when (f) {
            "Last 7 Days" -> repository.getDailyTotalsLast7Days(userId).asLiveData()
            "This Month"  -> repository.getDailyTotalsThisMonth(userId).asLiveData()
            "This Year"   -> repository.getDailyTotalsThisYear(userId).asLiveData()
            else          -> repository.getDailyTotals(userId).asLiveData()
        }
    }

    // ✅ Always-available monthly/yearly totals
    val monthlyTotals: LiveData<List<MonthlyTotal>> =
        repository.getMonthlyTotals(userId).asLiveData()

    val yearlyTotals: LiveData<List<YearlyTotal>> =
        repository.getYearlyTotals(userId).asLiveData()

    // ✅ Transaction count
    val transactionCount: LiveData<Int> =
        repository.getTransactionCount(userId).asLiveData()

    // ✅ Filtered raw transactions
    val filteredTransactions: LiveData<List<Transaction>> = _filter.switchMap { f ->
        when (f) {
            "Last 7 Days" -> repository.getTransactionsLast7Days(userId).asLiveData()
            "This Month"  -> repository.getTransactionsThisMonth(userId).asLiveData()
            "This Year"   -> repository.getTransactionsThisYear(userId).asLiveData()
            else          -> repository.getAllTransactions(userId).asLiveData()
        }
    }

    // ✅ Update filter safely
    fun updateFilter(newFilter: String) {
        if (_filter.value != newFilter) {
            _filter.value = newFilter
        }
    }

    // ✅ Insert transaction
    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insert(transaction)
        }
    }

    // ✅ Delete transaction
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

    // ✅ Clear all transactions
    fun clearAllForUser() {
        viewModelScope.launch {
            repository.clearAllTransactions(userId)
        }
    }
}
