package com.example.myfreehealthtracker.localdatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.Date

enum class TipoPasto {
    Colazione, Pranzo, Cena, Spuntino
}
@Entity(primaryKeys = ["UserID", "date"])
data class Pasto(
    @ColumnInfo(name = "UserID")
    var userID: String,
    @ColumnInfo(name = "date")
    var date: Date,
    @ColumnInfo(name = "typePasto")
    var typePasto: TipoPasto,
    @ColumnInfo(name = "note")
    var note: String

)
