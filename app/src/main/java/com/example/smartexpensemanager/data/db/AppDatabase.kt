package com.example.smartexpensemanager.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartexpensemanager.data.model.Transaction
import com.example.smartexpensemanager.data.model.TransactionDao
import com.example.smartexpensemanager.data.model.Expense
import com.example.smartexpensemanager.data.model.ExpenseDao

@Database(
    entities = [Transaction::class, Expense::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_expense_manager_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
