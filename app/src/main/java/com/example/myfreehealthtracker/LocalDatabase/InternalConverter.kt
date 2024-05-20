package com.example.myfreehealthtracker.LocalDatabase

import androidx.room.TypeConverter
import java.util.Date


class InternalConverter {
    @TypeConverter
    fun DateToString(date: Date): String {
        return date.toString()
    }


    @TypeConverter
    fun StringToDate(date: String): Date {
        return Date(date)
    }

    @TypeConverter
    fun ListPairStringIntToString(list: List<Pair<String, Int>>): String {
        return list.joinToString(",") { "${it.first}:${it.second}" }
    }

    @TypeConverter
    fun StringToListPairStringInt(string: String): List<Pair<String, Int>> {
        return string.split(",").map {
            val item = it.split(":")
            item[0] to item[1].toInt()
        }
    }

    @TypeConverter
    fun ListPairDateIntToString(list: List<Pair<Date, Int>>): String {
        return list.joinToString(",") { "${it.first.time}:${it.second}" }

    }

    @TypeConverter
    fun StringToListPairDateInt(string: String): List<Pair<Date, Int>> {
        return string.split(",").map {
            val item = it.split(":")
            Date(item[0]) to item[1].toInt()
        }
    }
}