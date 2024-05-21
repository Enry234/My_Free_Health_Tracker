package com.example.myfreehealthtracker.LocalDatabase

import androidx.room.TypeConverter
import java.util.Date


class InternalConverter {
    @TypeConverter
    fun DateToString(date: Date?): String {
        if (date == null) return ""
        return date.toString()
    }


    @TypeConverter
    fun StringToDate(date: String): Date {
        if (date == "") return Date()
        return Date(date)
    }

    @TypeConverter
    fun listPairStringIntToString(list: List<Pair<String, Int>>?): String {
        if (list != null) {
            return list.joinToString(",") { "${it.first}:${it.second}" }
        }
        return ""
    }

    @TypeConverter
    fun stringToListPairStringInt(string: String): List<Pair<String, Int>> {
        if (string == "") return listOf()
        return string.split(",").map {
            val item = it.split(":")
            item[0] to item[1].toInt()
        }
    }

    @TypeConverter
    fun listPairDateIntToString(list: List<Pair<Date, Int>>?): String {
        if (list != null) {
            return list.joinToString(",") { "${it.first.time}:${it.second}" }
        }
        return ""

    }

    @TypeConverter
    fun stringToListPairDateInt(string: String): List<Pair<Date, Int>> {
        if (string == "") return listOf()
        return string.split(",").map {
            val item = it.split(":")
            Date(item[0]) to item[1].toInt()
        }
    }
}