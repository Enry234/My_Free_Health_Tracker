package com.example.myfreehealthtracker.LocalDatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Medicine(
    @PrimaryKey
    var id: Int,
    @ColumnInfo(name = "nome")
    var nome: String,
    @ColumnInfo(name = "descrizione")
    var desc: String
)
