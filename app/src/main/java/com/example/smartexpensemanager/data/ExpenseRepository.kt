package com.example.smartexpensemanager.data

import android.content.Context
import com.example.smartexpensemanager.data.db.AppDatabase
import com.example.smartexpensemanager.data.model.Expense
import com.example.smartexpensemanager.data.model.ExpenseDao
import com.example.smartexpensemanager.data.model.CategoryTotal
import com.example.smartexpensemanager.data.model.DailyTotal
import com.example.smartexpensemanager.data.model.MonthlyTotal
import com.example.smartexpensemanager.data.model.YearlyTotal
import kotlinx.coroutines.flow.Flow

/**
 * Repository handles data operations for Expense entity.
 * Abstracts the data source from the rest of the app.
 */
class ExpenseRepository private constructor(
    private val expenseDao: ExpenseDao
) {

    // ✅ Insert a new expense
    suspend fun insertExpense(expense: Expense) {
        expenseDao.insert(expense)
    }

    // ✅ Delete an expense by passing the object
    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    // ✅ Delete an expense by ID
    suspend fun deleteExpenseById(id: Int) {
        expenseDao.deleteExpenseById(id)
    }

    // ✅ All expenses for a user
    fun getAllExpenses(userId: Int): Flow<List<Expense>> =
        expenseDao.getAllExpenses(userId)

    // ✅ Reactive totals (non-null Double from DAO)
    fun getTotalIncome(userId: Int): Flow<Double> =
        expenseDao.getTotalIncome(userId)

    fun getTotalExpenses(userId: Int): Flow<Double> =
        expenseDao.getTotalExpenses(userId)

    // ✅ Recent transactions
    fun getRecentTransactions(userId: Int): Flow<List<Expense>> =
        expenseDao.getRecentTransactions(userId)

    // ✅ Aggregated totals for reports
    fun getCategoryTotals(userId: Int): Flow<List<CategoryTotal>> =
        expenseDao.getCategoryTotals(userId)

    fun getDailyTotals(userId: Int): Flow<List<DailyTotal>> =
        expenseDao.getDailyTotals(userId)

    fun getMonthlyTotals(userId: Int): Flow<List<MonthlyTotal>> =
        expenseDao.getMonthlyTotals(userId)

    fun getYearlyTotals(userId: Int): Flow<List<YearlyTotal>> =
        expenseDao.getYearlyTotals(userId)

    companion object {
        @Volatile
        private var INSTANCE: ExpenseRepository? = null

        fun getInstance(context: Context): ExpenseRepository {
            return INSTANCE ?: synchronized(this) {
                val database = AppDatabase.getDatabase(context)
                val dao = database.expenseDao()
                ExpenseRepository(dao).also { INSTANCE = it }
            }
        }
    }
}
