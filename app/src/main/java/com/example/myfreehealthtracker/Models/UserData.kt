package com.example.myfreehealthtracker.Models

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
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
    var dataDiNascita: Date? = null,
    @ColumnInfo(name = "sesso")
    var sesso: Char = ' ',
    @ColumnInfo(name = "altezza")
    var altezza: Int = 0,
    @ColumnInfo(name = "posizione")
    var posizione: Location? = null,
    @ColumnInfo(name = "peso")
    var peso: MutableList<Pair<Date, Int>>? = null,
    @ColumnInfo(name = "imageProfile")
    var image: ImageVector? = null
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun addPeso(newPeso: Int) {
        peso!!.add(Pair(Date.from(Instant.now()), newPeso))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEta(): Int {
        return Date.from(Instant.now()).year - dataDiNascita!!.year
    }


}
