package com.example.smartexpensemanager.data.model

// Daily totals (used in line/bar chart or insights)
data class DailyTotal( val day: String,
// matches "date(...)" in SQL
val total: Double )
