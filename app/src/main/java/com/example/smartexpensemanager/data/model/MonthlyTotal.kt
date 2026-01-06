package com.example.smartexpensemanager.data.model

data class MonthlyTotal( val month: String,
// matches strftime('%m', ...)
 val total: Double )
