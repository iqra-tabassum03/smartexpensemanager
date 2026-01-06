package com.example.smartexpensemanager.ui.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensemanager.data.model.Transaction
import com.example.smartexpensemanager.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying a list of Transactions in a RecyclerView.
 * Supports delete action via callback.
 */
class TransactionAdapter(
    private val onDeleteClicked: (Transaction) -> Unit // callback for delete
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.textTitle.text = transaction.title
            binding.textCategory.text = transaction.category

            // Format amount with + or - prefix
            val formattedAmount = formatCurrency(transaction.amount, transaction.type)
            binding.textAmount.text = formattedAmount

            // Resolve theme-aware color attributes
            val colorAttr = if (transaction.type.equals("Income", ignoreCase = true)) {
                com.example.smartexpensemanager.R.attr.incomeColor
            } else {
                com.example.smartexpensemanager.R.attr.expenseColor
            }
            val typedValue = TypedValue()
            val theme = binding.root.context.theme
            theme.resolveAttribute(colorAttr, typedValue, true)
            val resolvedColor = typedValue.data

            // Apply resolved color
            binding.textAmount.setTextColor(resolvedColor)
            binding.iconIndicator.setColorFilter(resolvedColor)

            // Format timestamp
            binding.textDate.text = formatDate(transaction.timestamp)

            // Accessibility
            binding.textAmount.contentDescription =
                "${transaction.type} amount $formattedAmount"
            binding.iconIndicator.contentDescription =
                "${transaction.type} category icon"

            // Delete button
            binding.btnDelete.setOnClickListener {
                onDeleteClicked(transaction)
            }
        }

        private fun formatCurrency(amount: Double, type: String): String {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            val formatted = format.format(amount)
            return if (type.equals("Income", ignoreCase = true)) {
                "+$formatted"
            } else {
                "-$formatted"
            }
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            oldItem == newItem
    }
}
