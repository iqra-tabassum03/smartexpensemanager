package com.example.smartexpensemanager.ui

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartexpensemanager.MainActivity
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.databinding.FragmentProfileBinding
import com.example.smartexpensemanager.ui.auth.LoginActivity
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserInfo()
        setupListeners()
    }

    private fun loadUserInfo() {
        val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", "Iqra Tabassum")
        val email = prefs.getString("user_email", "iqratabassum@example.com")

        binding.tvUserName.text = name
        binding.tvUserEmail.text = email
        binding.tvAppVersion?.text = "alpha 0.7"
    }

    private fun setupListeners() {
        // Edit Profile
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_editProfileFragment)
        }

        // Change Password
        binding.btnChangePassword.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
            val etOldPassword = dialogView.findViewById<EditText>(R.id.etOldPassword)
            val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)
            val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Save") { _, _ ->
                    val oldPass = etOldPassword.text.toString().trim()
                    val newPass = etNewPassword.text.toString().trim()
                    val confirmPass = etConfirmPassword.text.toString().trim()

                    val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val savedPassword = prefs.getString("user_password", null)

                    when {
                        oldPass != savedPassword -> {
                            Snackbar.make(binding.root, "Current password is incorrect", Snackbar.LENGTH_LONG).show()
                        }
                        newPass.isEmpty() || confirmPass.isEmpty() -> {
                            Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_LONG).show()
                        }
                        newPass != confirmPass -> {
                            Snackbar.make(binding.root, "Passwords do not match", Snackbar.LENGTH_LONG).show()
                        }
                        else -> {
                            prefs.edit().putString("user_password", newPass).apply()
                            Snackbar.make(binding.root, "Password updated successfully!", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.show()
        }

        // About
        binding.btnAbout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("About Smart Expense Manager")
                .setMessage("Version alpha 0.7\nDeveloped by Iqra Tabassum")
                .setPositiveButton("OK", null)
                .show()
        }

        // Notifications → open system notification settings
        binding.btnNotifications.setOnClickListener {
            val intent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                }
            } else {
                Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${requireContext().packageName}")
                }
            }
            startActivity(intent)
        }

        // Reminder Time → let user pick their own reminder time
        binding.btnReminderTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)

            TimePickerDialog(requireContext(), { _, pickedHour, pickedMinute ->
                val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putInt("reminder_hour", pickedHour)
                    .putInt("reminder_minute", pickedMinute)
                    .apply()

                Snackbar.make(binding.root, "Reminder set for $pickedHour:$pickedMinute", Snackbar.LENGTH_LONG).show()

                // Re‑schedule with new time
                (requireActivity() as MainActivity).scheduleDailyReminder()
            }, hour, minute, true).show()
        }

        // Privacy Policy
        binding.btnPrivacyPolicy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://your-privacy-policy-url.com"))
            startActivity(intent)
        }

        // Help & Support
        binding.btnHelpSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://your-help-support-url.com"))
            startActivity(intent)
        }

        // Feedback
        binding.btnFeedback.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:iqratabassum@example.com")
                putExtra(Intent.EXTRA_SUBJECT, "Smart Expense Manager Feedback")
            }
            startActivity(emailIntent)
        }

        // Dark Mode toggle
        binding.btnDarkMode?.setOnClickListener {
            val currentMode = AppCompatDelegate.getDefaultNightMode()
            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            Snackbar.make(binding.root, "Logged out successfully!", Snackbar.LENGTH_LONG).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainActivity)?.hideFab()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
