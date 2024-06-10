package com.example.myfreehealthtracker.LocalDatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sport(
    @PrimaryKey //generate by Firebase
    var id: String = "",
    @ColumnInfo("NomeSport")
    var nomeSport: String = "",
    @ColumnInfo("Descrizione")
    var desc: String = "",
    @ColumnInfo("Immagine")
    var image: String = ""
)
