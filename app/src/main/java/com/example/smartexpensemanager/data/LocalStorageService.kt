package com.example.smartexpensemanager.data

import android.content.Context
import com.example.smartexpensemanager.data.model.Transaction
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class LocalStorageService(context: Context) {

    private val prefs = context.getSharedPreferences("ExpenseManagerData", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun getTransactions(): List<Transaction> {
        val jsonString = prefs.getString(TRANSACTIONS_KEY, null)
        return if (jsonString.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                json.decodeFromString<List<Transaction>>(jsonString)
            } catch (e: Exception) {
                println("Error deserializing transactions: ${e.message}")
                emptyList()
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        val currentList = getTransactions().toMutableList()
        currentList.add(0, transaction)
        saveAllTransactions(currentList)
    }

    private fun saveAllTransactions(transactions: List<Transaction>) {
        try {
            val jsonString = json.encodeToString(transactions)
            prefs.edit().putString(TRANSACTIONS_KEY, jsonString).apply()
        } catch (e: Exception) {
            println("Error serializing transactions: ${e.message}")
        }
    }

    companion object {
        private const val TRANSACTIONS_KEY = "transactions_list_json"

        @Volatile
        private var INSTANCE: LocalStorageService? = null

        fun getInstance(context: Context): LocalStorageService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalStorageService(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
