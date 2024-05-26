package com.example.myfreehealthtracker.LocalDatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.Date

@Entity(primaryKeys = ["idUserID", "idDate", "idAlimentoId"])
data class PastoToCibo(
    @ColumnInfo(name = "idUserID")
    var userID: String,
    @ColumnInfo(name = "idDate")
    var date: Date,
    @ColumnInfo(name = "idAlimentoId")
    var idAlimento: Int,
    @ColumnInfo(name = "quantity")
    var quantity: Int
)
