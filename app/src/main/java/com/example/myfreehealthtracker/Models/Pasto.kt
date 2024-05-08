package com.example.myfreehealthtracker.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.Date

@Entity(primaryKeys = ["userID", "date"])
data class Pasto(
    @ColumnInfo(name = "UserID")
    var userID: String,
    @ColumnInfo(name = "date")
    var date: Date,
    @ColumnInfo(name = "typePasto")
    var typePasto: Int, //0=colazione,1=pranzo,2=cena,3=spuntino
    @ColumnInfo(name = "note")
    var note: String

)
