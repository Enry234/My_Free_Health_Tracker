package com.example.myfreehealthtracker.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.Date

@Entity(primaryKeys = ["UserId", "Date", "IdMedicina"])
data class Assunzione(
    @ColumnInfo(name = "UserID")
    var userID: String,
    @ColumnInfo(name = "Date")
    var date: Date,
    @ColumnInfo(name = "IdMedicina")
    var idMedicina: Int,
    @ColumnInfo(name = "Quantita")
    var quantita: Int,
    @ColumnInfo(name = "Note")
    var note: String

)
