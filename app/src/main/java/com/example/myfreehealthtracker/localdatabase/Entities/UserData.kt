package com.example.myfreehealthtracker.localdatabase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "User")
class UserData(
    @ColumnInfo(name = "nome")
    var nome: String = "",
    @ColumnInfo(name = "Cognome")
    var cognome: String = "",
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(name = "Data_di_nascita")
    var dataDiNascita: String = "",
    @ColumnInfo(name = "sesso")
    var sesso: String = "",
    @ColumnInfo(name = "altezza")
    var altezza: Int = 0,
    @ColumnInfo(name = "posizione")
    var posizione: String = "",
    @ColumnInfo(name = "peso")
    var peso: MutableList<Pair<Date, Double>>? = mutableListOf(),
    @ColumnInfo(name = "imageProfile")
    var image: String = "",
    @ColumnInfo(name = "calorieObiettivo")
    var calorie: Int = 0,
    @ColumnInfo(name = "proteine")
    var proteine: Int = 0,
    @ColumnInfo(name = "carboidrati")
    var carboidrati: Int = 0,
    @ColumnInfo(name = "grassi")
    var grassi: Int = 0,
    @ColumnInfo(name = "fibre")
    var fibre: Int = 0,
) {


}
