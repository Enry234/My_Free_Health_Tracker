package com.example.myfreehealthtracker.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    var sesso: Char = ' ',
    @ColumnInfo(name = "altezza")
    var altezza: Int = 0,
    @ColumnInfo(name = "posizione")
    var posizione: String = "",
//    @ColumnInfo(name = "peso")
//    var peso: MutableList<Pair<Date, Int>>? = null,
    @ColumnInfo(name = "imageProfile")
    var image: String = ""
) {

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun addPeso(newPeso: Int) {
//        peso!!.add(Pair(Date.from(Instant.now()), newPeso))
//    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getEta(): Int {
//        return Date.from(Instant.now()).year - dataDiNascita!!.year
//    }


}
