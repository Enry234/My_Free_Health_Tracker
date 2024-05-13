package com.example.myfreehealthtracker.Models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["UserID", "Date", "IdMedicina"])
data class Assunzione(
    @ColumnInfo(name = "UserID")
    var userID: String,
    @ColumnInfo(name = "Date")
    var date: String,
    @ColumnInfo(name = "IdMedicina")
    var idMedicina: Int,
    @ColumnInfo(name = "Quantita")
    var quantita: Int,
    @ColumnInfo(name = "Note")
    var note: String

)
