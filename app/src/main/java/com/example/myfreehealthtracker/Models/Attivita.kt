package com.example.myfreehealthtracker.Models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["UserId", "IdSport", "Date"])
data class Attivita(
    @ColumnInfo(name = "UserId")
    var userId: String,
    @ColumnInfo(name = "IdSport")
    var idSport: String,
    @ColumnInfo(name = "Date")
    var date: String,
    @ColumnInfo(name = "Note")
    var note: String,
    @ColumnInfo(name = "Calorie")
    var calorie: Int,
    @ColumnInfo(name = "Durata")
    var durata: Int,
    @ColumnInfo(name = "Distanza")
    var distanza: Int


)
