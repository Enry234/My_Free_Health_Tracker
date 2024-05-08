package com.example.myfreehealthtracker.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sport(
    @PrimaryKey
    var idSport: String,
    @ColumnInfo(name = "Descrizione")
    var desc: String
)
