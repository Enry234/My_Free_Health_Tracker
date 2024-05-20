package com.example.myfreehealthtracker.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alimento(
    @PrimaryKey
    var id: Int,
    @ColumnInfo(name = "nome")
    var nome: String,
    @ColumnInfo(name = "nutrienti")
    var nutrienti: List<Pair<String, Int>>,
    @ColumnInfo(name = "descrizione")
    var descrizione: String


)
