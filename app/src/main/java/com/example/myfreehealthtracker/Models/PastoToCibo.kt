package com.example.myfreehealthtracker.Models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["idUserID", "idDate", "idAlimentoId"])
data class PastoToCibo(
    @ColumnInfo(name = "idUserID")
    var userID: String,
    @ColumnInfo(name = "idDate")
    var date: String,
    @ColumnInfo(name = "idAlimentoId")
    var idAlimento: Int,
    @ColumnInfo(name = "quantity")
    var quantity: Int
)
