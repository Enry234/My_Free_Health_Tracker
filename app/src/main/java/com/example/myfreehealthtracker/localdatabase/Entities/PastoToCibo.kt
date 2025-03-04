package com.example.myfreehealthtracker.localdatabase.Entities

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
    var idAlimento: String,
    @ColumnInfo(name = "quantity")
    var quantity: Float
)
