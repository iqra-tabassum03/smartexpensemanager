package com.example.smartexpensemanager.data

import android.content.Context
import com.example.smartexpensemanager.data.db.AppDatabase
import com.example.smartexpensemanager.data.model.Transaction
import com.example.smartexpensemanager.data.model.TransactionDao
import com.example.smartexpensemanager.data.model.CategoryTotal
import com.example.smartexpensemanager.data.model.DailyTotal
import com.example.smartexpensemanager.data.model.MonthlyTotal
import com.example.smartexpensemanager.data.model.YearlyTotal
import kotlinx.coroutines.flow.Flow

class TransactionRepository private constructor(context: Context) {

    private val transactionDao: TransactionDao =
        AppDatabase.getDatabase(context).transactionDao()

    // --- Raw transactions ---
    fun getAllTransactions(userId: Int): Flow<List<Transaction>> =
        transactionDao.getAll(userId)

    fun getTransactionsLast7Days(userId: Int): Flow<List<Transaction>> =
        transactionDao.getTransactionsLast7Days(userId)

    fun getTransactionsThisMonth(userId: Int): Flow<List<Transaction>> =
        transactionDao.getTransactionsThisMonth(userId)

    fun getTransactionsThisYear(userId: Int): Flow<List<Transaction>> =
        transactionDao.getTransactionsThisYear(userId)

    suspend fun insert(transaction: Transaction) =
        transactionDao.insert(transaction)

    suspend fun delete(transaction: Transaction) =
        transactionDao.delete(transaction)

    // --- Totals (unfiltered) ---
    fun getCategoryTotals(userId: Int): Flow<List<CategoryTotal>> =
        transactionDao.getCategoryTotals(userId)

    fun getDailyTotals(userId: Int): Flow<List<DailyTotal>> =
        transactionDao.getDailyTotals(userId)

    fun getMonthlyTotals(userId: Int): Flow<List<MonthlyTotal>> =
        transactionDao.getMonthlyTotals(userId)

    fun getYearlyTotals(userId: Int): Flow<List<YearlyTotal>> =
        transactionDao.getYearlyTotals(userId)

    // --- Totals (filtered) ---
    fun getCategoryTotalsLast7Days(userId: Int): Flow<List<CategoryTotal>> =
        transactionDao.getCategoryTotalsLast7Days(userId)

    fun getCategoryTotalsThisMonth(userId: Int): Flow<List<CategoryTotal>> =
        transactionDao.getCategoryTotalsThisMonth(userId)

    fun getCategoryTotalsThisYear(userId: Int): Flow<List<CategoryTotal>> =
        transactionDao.getCategoryTotalsThisYear(userId)

    fun getDailyTotalsLast7Days(userId: Int): Flow<List<DailyTotal>> =
        transactionDao.getDailyTotalsLast7Days(userId)

    fun getDailyTotalsThisMonth(userId: Int): Flow<List<DailyTotal>> =
        transactionDao.getDailyTotalsThisMonth(userId)

    fun getDailyTotalsThisYear(userId: Int): Flow<List<DailyTotal>> =
        transactionDao.getDailyTotalsThisYear(userId)

    // --- Income/Expense totals ---
    fun getTotalIncome(userId: Int): Flow<Double> =
        transactionDao.getTotalIncome(userId)

    fun getTotalExpenses(userId: Int): Flow<Double> =
        transactionDao.getTotalExpenses(userId)

    fun getTransactionCount(userId: Int): Flow<Int> =
        transactionDao.countForUser(userId)

    suspend fun clearAllTransactions(userId: Int) =
        transactionDao.clearAllForUser(userId)

    companion object {
        @Volatile
        private var INSTANCE: TransactionRepository? = null

        fun getInstance(context: Context): TransactionRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TransactionRepository(context).also { INSTANCE = it }
            }
        }
    }
}
