package com.example.myfreehealthtracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.Instant
import java.util.Date

class UserData(
    var nome: String = "",
    var cognome: String = "",
    var id: String = "",
    var dataDiNascita: Date? = null,
    var sesso: Char = ' ',
    var altezza: Int = 0,
    var posizione: String = " ",
    var peso: MutableList<Pair<Date, Int>>? = null,
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