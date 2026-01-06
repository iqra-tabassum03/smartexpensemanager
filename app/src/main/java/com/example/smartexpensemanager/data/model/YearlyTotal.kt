package com.example.smartexpensemanager.data.model

data class YearlyTotal( val year: String,
// matches strftime('%Y', ...)
 val total: Double )