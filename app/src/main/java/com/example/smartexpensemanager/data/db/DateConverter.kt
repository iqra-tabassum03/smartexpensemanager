package com.example.smartexpensemanager.data.db

import androidx.room.TypeConverter
import java.util.Date

/**
 * Defines converters for Room to handle non-primitive types (like Date)
 */
class DateConverter { // <--- CLASS NAME MUST MATCH FILE NAME (DateConverter)

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}