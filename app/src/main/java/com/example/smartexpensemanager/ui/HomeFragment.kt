package com.example.smartexpensemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexpensemanager.SmartExpenseManagerApplication
import com.example.smartexpensemanager.databinding.FragmentHomeBinding
import com.example.smartexpensemanager.ui.adapter.TransactionAdapter
import com.example.smartexpensemanager.viewmodel.ReportsViewModel
import com.example.smartexpensemanager.viewmodel.ReportsViewModelFactory
import com.example.smartexpensemanager.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.example.smartexpensemanager.data.model.Transaction

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val reportsViewModel: ReportsViewModel by activityViewModels {
        ReportsViewModelFactory(
            (requireActivity().application as SmartExpenseManagerApplication).container.transactionRepository
        )
    }

    private lateinit var adapter: TransactionAdapter
    private val userId = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView with delete callback
        adapter = TransactionAdapter { transaction ->
            showDeleteConfirm(transaction)
        }
        binding.recyclerTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTransactions.adapter = adapter

        // Observe transactions and compute everything from one source
        reportsViewModel.filteredTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)

            val totalIncome = transactions.filter { it.type.equals("Income", ignoreCase = true) }
                .sumOf { it.amount }
            val totalExpenses = transactions.filter { it.type.equals("Expense", ignoreCase = true) }
                .sumOf { it.amount }
            val balance = totalIncome - totalExpenses

            binding.textCurrentBalance.text = "₨%.2f".format(balance)
            binding.textTotalIncome.text = "Income: ₨%.2f".format(totalIncome)
            binding.textTotalExpenses.text = "Expenses: ₨%.2f".format(totalExpenses)

            android.util.Log.d("HomeFragment", "Transactions count: ${transactions.size}")
        }
    }

    // Confirmation dialog
    private fun showDeleteConfirm(transaction: Transaction) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete transaction")
            .setMessage("Are you sure you want to delete \"${transaction.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                reportsViewModel.deleteTransaction(transaction)
                showUndoSnackbar(transaction)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Undo snackbar
    private fun showUndoSnackbar(transaction: Transaction) {
        Snackbar.make(binding.root, "Transaction deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                reportsViewModel.insertTransaction(transaction)
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainActivity)?.showFab()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
