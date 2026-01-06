package com.example.smartexpensemanager.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.smartexpensemanager.MainActivity
import com.example.smartexpensemanager.SmartExpenseManagerApplication
import com.example.smartexpensemanager.databinding.FragmentReportsBinding
import com.example.smartexpensemanager.viewmodel.ReportsViewModel
import com.example.smartexpensemanager.viewmodel.ReportsViewModelFactory
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val reportsViewModel: ReportsViewModel by activityViewModels {
        ReportsViewModelFactory(
            (requireActivity().application as SmartExpenseManagerApplication).container.transactionRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Debug: observe transaction count
        reportsViewModel.transactionCount.observe(viewLifecycleOwner) { count ->
            Log.d("DB_CHECK", "Transactions for user 1: $count")
        }

        // ✅ Observe DAO-driven category totals (always current)
        reportsViewModel.categoryTotals.observe(viewLifecycleOwner) { totals ->
            if (totals.isEmpty()) {
                binding.pieChart.clear()
                binding.tvInsights.text = "No data for selected period."
                return@observe
            }
            val mapped = totals.associate { it.category to it.total }
            updatePieChart(mapped)
            updateRecommendations(mapped)
        }

        // ✅ Show current filter
        reportsViewModel.filter.observe(viewLifecycleOwner) { filter ->
            binding.tvFilter.text = "Filter: $filter"
        }

        setupFilters()

        // ✅ Observe filtered transactions directly
        reportsViewModel.filteredTransactions.observe(viewLifecycleOwner) { transactions ->
            Log.d("DB_CHECK", "Fetched transactions: $transactions")
            if (transactions.isEmpty()) {
                binding.pieChart.clear()
                binding.tvInsights.text = "No data for selected period."
                return@observe
            }
            val totals = transactions.groupBy { it.category }
                .map { it.key to it.value.sumOf { tx -> tx.amount } }
                .toMap()
            updatePieChart(totals)
            updateRecommendations(totals)
        }
    }

    private fun setupFilters() {
        binding.chipGroupFilters.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.chipWeekly.id -> {
                    reportsViewModel.updateFilter("Last 7 Days")
                    binding.tvInsights.text = "Weekly report selected."
                }
                binding.chipMonthly.id -> {
                    reportsViewModel.updateFilter("This Month")
                    binding.tvInsights.text = "Monthly report selected."
                }
                binding.chipYearly.id -> {
                    reportsViewModel.updateFilter("This Year")
                    binding.tvInsights.text = "Yearly report selected."
                }
            }
        }
    }

    private fun updatePieChart(totals: Map<String, Double>) {
        val entries = totals.map { (category, sum) -> PieEntry(sum.toFloat(), category) }
        val dataSet = PieDataSet(entries, getString(com.example.smartexpensemanager.R.string.label_category)).apply {
            colors = listOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#F44336"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#9C27B0"),
                Color.parseColor("#009688")
            )
            valueTextSize = 14f
            valueTextColor = Color.WHITE
            sliceSpace = 2f
        }
        binding.pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            setHoleColor(Color.TRANSPARENT)
            setDrawEntryLabels(true)
            animateY(800)
            invalidate()
        }
    }

    private fun updateRecommendations(totals: Map<String, Double>) {
        if (totals.isEmpty()) {
            binding.tvInsights.text = "No data available."
            return
        }

        val totalSum = totals.values.sum()
        val sorted = totals.entries.sortedByDescending { it.value }
        val top = sorted.first()
        val topPercent = if (totalSum > 0) (top.value / totalSum * 100).toInt() else 0

        val second = sorted.getOrNull(1)
        val secondPercent = if (second != null && totalSum > 0) (second.value / totalSum * 100).toInt() else 0

        val smallCategories = sorted.filter { totalSum > 0 && (it.value / totalSum * 100) < 5 }.map { it.key }

        val builder = StringBuilder().apply {
            append("• Biggest expense: ${top.key} — ₨${"%.0f".format(top.value)} (${topPercent}%)\n")
            if (second != null) {
                append("• Next: ${second.key} — ₨${"%.0f".format(second.value)} (${secondPercent}%)\n")
            }
            if (topPercent >= 35) {
                append("• Tip: ${top.key} is dominating your budget. Set a weekly cap or switch to lower-cost alternatives.\n")
            } else {
                append("• Tip: Your spending is fairly balanced. Keep tracking and aim for a fixed savings percentage.\n")
            }
            if (smallCategories.isNotEmpty()) {
                append("• Low-impact categories: ${smallCategories.joinToString(", ")} — consider consolidating or reviewing if they’re necessary.\n")
            }
        }

        binding.tvInsights.text = builder.toString().trim()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideFab()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
