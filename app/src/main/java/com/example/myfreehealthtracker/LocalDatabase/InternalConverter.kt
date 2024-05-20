package com.example.myfreehealthtracker.LocalDatabase

import androidx.room.TypeConverter
import java.util.Date

data class EncodedPair(val key: String, val value: Int)

class InternalConverter {
    @TypeConverter
    fun DateToString(date: Date): String {
        return date.toString()
    }


    @TypeConverter
    fun StringToDate(date: String): Date {
        return Date(date)
    }
}