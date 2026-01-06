package com.example.smartexpensemanager.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smartexpensemanager.SmartExpenseManagerApplication
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.databinding.DialogAddTransactionBinding
import com.example.smartexpensemanager.viewmodel.ExpenseViewModel
import com.example.smartexpensemanager.viewmodel.ExpenseViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.button.MaterialButtonToggleGroup
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "AddTransactionDialogFragment"
        private const val TYPE_EXPENSE = "Expense"
        private const val TYPE_INCOME = "Income"
    }

    private var _binding: DialogAddTransactionBinding? = null
    private val binding get() = _binding!!

    private var currentType: String = TYPE_EXPENSE
    private var selectedDate: Date = Date()
    private val dateFormatForDisplay = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private val expenseViewModel: ExpenseViewModel by activityViewModels {
        ExpenseViewModelFactory(
            (requireActivity().application as SmartExpenseManagerApplication).container.expenseRepository
        )
    }

    private val categories: List<String>
        get() = if (currentType == TYPE_EXPENSE) {
            resources.getStringArray(R.array.expense_categories).toList()
        } else {
            resources.getStringArray(R.array.income_categories).toList()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTypeToggleGroup()
        setupCategorySpinner()
        setupDatePicker()
        setupActionListeners()

        updateDateTextView(selectedDate)
    }

    private fun setupTypeToggleGroup() {
        binding.typeToggleGroup.addOnButtonCheckedListener { _: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                when (checkedId) {
                    R.id.toggle_expense -> setType(TYPE_EXPENSE)
                    R.id.toggle_income -> setType(TYPE_INCOME)
                }
            }
        }
        binding.typeToggleGroup.check(R.id.toggle_expense)
    }

    private fun setType(type: String) {
        currentType = type
        setupCategorySpinner()
        binding.tilDescription.hint = getString(R.string.hint_transaction_title)
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        binding.spinnerCategory.setAdapter(adapter)
        binding.spinnerCategory.setText("", false)
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener { showDatePicker() }
        binding.tilDate.setEndIconOnClickListener { showDatePicker() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance().apply { time = selectedDate }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            calendar.set(y, m, d)
            selectedDate = calendar.time
            updateDateTextView(selectedDate)
        }

        DatePickerDialog(requireContext(), dateSetListener, year, month, day).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    private fun updateDateTextView(date: Date) {
        binding.etDate.setText(dateFormatForDisplay.format(date))
    }

    private fun setupActionListeners() {
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSave.setOnClickListener { saveExpense() }
    }

    private fun saveExpense() {
        val title = binding.etDescription.text.toString().trim()
        val amountStr = binding.etAmount.text.toString().trim()
        val category = binding.spinnerCategory.text.toString().trim()
        val type = currentType

        if (title.isEmpty()) {
            binding.etDescription.error = getString(R.string.error_empty_field)
            return
        }

        val amountDouble = amountStr.toDoubleOrNull()
        if (amountDouble == null || amountDouble <= 0) {
            binding.etAmount.error = getString(R.string.error_invalid_amount)
            return
        }

        if (category.isEmpty()) {
            binding.spinnerCategory.error = getString(R.string.error_select_category)
            return
        }

        val timestamp = selectedDate.time
        val userId = 1 // TODO: replace with actual logged-in user id

        expenseViewModel.saveExpense(title, amountDouble, type, category, timestamp, userId)

        // ✅ Show Snack bar anchored to CoordinatorLayout root
        val coordinator = requireActivity().findViewById<View>(R.id.coordinator_root)
        Snackbar.make(coordinator, getString(R.string.transaction_saved_success), Snackbar.LENGTH_SHORT)
            .show()

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
