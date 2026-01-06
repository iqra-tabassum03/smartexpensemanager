package com.example.smartexpensemanager.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // ✅ Insert or update an expense
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    // ✅ Get all expenses for a user, newest first
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllExpenses(userId: Int): Flow<List<Expense>>

    // ✅ Total income for a user (always returns a non-null Double)
    @Query("SELECT IFNULL(SUM(amount), 0) FROM expenses WHERE userId = :userId AND type = 'Income'")
    fun getTotalIncome(userId: Int): Flow<Double>

    // ✅ Total expenses for a user (always returns a non-null Double)
    @Query("SELECT IFNULL(SUM(amount), 0) FROM expenses WHERE userId = :userId AND type = 'Expense'")
    fun getTotalExpenses(userId: Int): Flow<Double>

    // ✅ Recent 5 transactions
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY timestamp DESC LIMIT 5")
    fun getRecentTransactions(userId: Int): Flow<List<Expense>>

    // ✅ Category totals (for pie chart)
    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE userId = :userId GROUP BY category")
    fun getCategoryTotals(userId: Int): Flow<List<CategoryTotal>>

    // ✅ Daily totals (for line chart)
    @Query("SELECT date(timestamp/1000, 'unixepoch') as day, SUM(amount) as total FROM expenses WHERE userId = :userId GROUP BY day ORDER BY day ASC")
    fun getDailyTotals(userId: Int): Flow<List<DailyTotal>>

    // ✅ Monthly totals
    @Query("SELECT strftime('%m', timestamp/1000, 'unixepoch') as month, SUM(amount) as total FROM expenses WHERE userId = :userId GROUP BY month ORDER BY month ASC")
    fun getMonthlyTotals(userId: Int): Flow<List<MonthlyTotal>>

    // ✅ Yearly totals
    @Query("SELECT strftime('%Y', timestamp/1000, 'unixepoch') as year, SUM(amount) as total FROM expenses WHERE userId = :userId GROUP BY year ORDER BY year ASC")
    fun getYearlyTotals(userId: Int): Flow<List<YearlyTotal>>

    // ✅ Delete a single expense by passing the object
    @Delete
    suspend fun deleteExpense(expense: Expense)

    // ✅ Delete a single expense by ID
    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Int)
}
