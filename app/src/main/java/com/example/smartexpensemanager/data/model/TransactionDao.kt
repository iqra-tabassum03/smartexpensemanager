package com.example.smartexpensemanager.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAll(userId: Int): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    // --- Unfiltered totals ---
    @Query("SELECT category, IFNULL(SUM(amount),0) as total FROM transactions WHERE userId = :userId GROUP BY category")
    fun getCategoryTotals(userId: Int): Flow<List<CategoryTotal>>

    @Query("SELECT date(timestamp/1000, 'unixepoch') as day, IFNULL(SUM(amount),0) as total FROM transactions WHERE userId = :userId GROUP BY day ORDER BY day ASC")
    fun getDailyTotals(userId: Int): Flow<List<DailyTotal>>

    @Query("""
        SELECT strftime('%m', timestamp/1000, 'unixepoch') as month, IFNULL(SUM(amount),0) as total
        FROM transactions
        WHERE userId = :userId
        GROUP BY month
        ORDER BY month ASC
    """)
    fun getMonthlyTotals(userId: Int): Flow<List<MonthlyTotal>>

    @Query("""
        SELECT strftime('%Y', timestamp/1000, 'unixepoch') as year, IFNULL(SUM(amount),0) as total
        FROM transactions
        WHERE userId = :userId
        GROUP BY year
        ORDER BY year ASC
    """)
    fun getYearlyTotals(userId: Int): Flow<List<YearlyTotal>>

    // --- Filtered transactions ---
    @Query("SELECT * FROM transactions WHERE userId = :userId AND timestamp >= strftime('%s','now','-7 days')*1000 ORDER BY timestamp DESC")
    fun getTransactionsLast7Days(userId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND strftime('%m', timestamp/1000, 'unixepoch') = strftime('%m','now') AND strftime('%Y', timestamp/1000, 'unixepoch') = strftime('%Y','now') ORDER BY timestamp DESC")
    fun getTransactionsThisMonth(userId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND strftime('%Y', timestamp/1000, 'unixepoch') = strftime('%Y','now') ORDER BY timestamp DESC")
    fun getTransactionsThisYear(userId: Int): Flow<List<Transaction>>

    // --- Filtered category totals ---
    @Query("""
        SELECT category, IFNULL(SUM(amount),0) as total
        FROM transactions
        WHERE userId = :userId
          AND timestamp >= strftime('%s','now','-7 days')*1000
        GROUP BY category
    """)
    fun getCategoryTotalsLast7Days(userId: Int): Flow<List<CategoryTotal>>

    @Query("""
        SELECT category, IFNULL(SUM(amount),0) as total
        FROM transactions
        WHERE userId = :userId
          AND strftime('%m', timestamp/1000, 'unixepoch') = strftime('%m','now')
          AND strftime('%Y', timestamp/1000, 'unixepoch') = strftime('%Y','now')
        GROUP BY category
    """)
    fun getCategoryTotalsThisMonth(userId: Int): Flow<List<CategoryTotal>>

    @Query("""
        SELECT category, IFNULL(SUM(amount),0) as total
        FROM transactions
        WHERE userId = :userId
          AND strftime('%Y', timestamp/1000, 'unixepoch') = strftime('%Y','now')
        GROUP BY category
    """)
    fun getCategoryTotalsThisYear(userId: Int): Flow<List<CategoryTotal>>

    // --- Filtered daily totals ---
    @Query("""
        SELECT date(timestamp/1000, 'unixepoch') as day, IFNULL(SUM(amount),0) as total
        FROM transactions
        WHERE userId = :userId
          AND timestamp >= strftime('%s','now','-7 days')*1000
        GROUP BY day
        ORDER BY day ASC
    """)
    fun getDailyTotalsLast7Days(userId: Int): Flow<List<DailyTotal>>

    @Query("""
        SELECT date(timestamp/1000, 'unixepoch') as day, IFNULL(SUM(amount),0) as total
        FROM transactions
        WHERE userId = :userId
          AND strftime('%m', timestamp/1000, 'unixepoch') = strftime('%m','now')
          AND strftime('%Y', timestamp/1000, 'unixepoch') = strftime('%Y','now')
        GROUP BY day
        ORDER BY day ASC
    """)
    fun getDailyTotalsThisMonth(userId: Int): Flow<List<DailyTotal>>

    @Query("""
        SELECT date(timestamp/1000, 'unixepoch') as day, IFNULL(SUM(amount),0) as total
        FROM transactions
        WHERE userId = :userId
          AND strftime('%Y', timestamp/1000, 'unixepoch') = strftime('%Y','now')
        GROUP BY day
        ORDER BY day ASC
    """)
    fun getDailyTotalsThisYear(userId: Int): Flow<List<DailyTotal>>

    // --- Totals by type ---
    @Query("SELECT IFNULL(SUM(amount),0) FROM transactions WHERE userId = :userId AND type = 'Income'")
    fun getTotalIncome(userId: Int): Flow<Double>

    @Query("SELECT IFNULL(SUM(amount),0) FROM transactions WHERE userId = :userId AND type = 'Expense'")
    fun getTotalExpenses(userId: Int): Flow<Double>

    @Query("SELECT COUNT(*) FROM transactions WHERE userId = :userId")
    fun countForUser(userId: Int): Flow<Int>

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun clearAllForUser(userId: Int)
}
