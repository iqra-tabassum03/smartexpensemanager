package com.example.smartexpensemanager.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * Utility class to manage and apply the application's theme settings (Dark/Light mode).
 * Uses SharedPreferences for simple local persistence of the user's preference.
 */
object ThemeManager {

    private const val PREFS_NAME = "AppPrefs"
    private const val KEY_DARK_MODE = "dark_mode_enabled"

    /**
     * Applies the saved theme preference or the default (System Default).
     */
    fun applySavedTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Default to false (Light Mode) if no preference is saved
        val isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false)

        // Sets the mode. MODE_NIGHT_NO is Light, MODE_NIGHT_YES is Dark
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * Saves the new theme setting and applies it immediately.
     */
    fun setDarkMode(context: Context, isDarkMode: Boolean) {
        // 1. Save preference
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, isDarkMode)
            .apply()

        // 2. Apply theme immediately
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * Retrieves the current saved state of the dark mode setting.
     */
    fun isDarkModeEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)
    }
}