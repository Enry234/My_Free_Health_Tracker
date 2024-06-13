package com.example.myfreehealthtracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.myfreehealthtracker.LocalDatabase.InternalDatabase
import com.example.myfreehealthtracker.LocalDatabase.Repositories.AlimentoRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.AttivitaRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.PastoRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.PastoToCiboRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.SportRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.UserRepository
import com.example.myfreehealthtracker.LocalDatabase.ViewModels.UserDataViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.File

class MainApplication : Application() {


    private val internalDatabaseRef by lazy { InternalDatabase.getDatabase(this) }
    private val userDao by lazy { internalDatabaseRef.userDao() }
    private val alimentoDao by lazy { internalDatabaseRef.alimentoDao() }
    private val pastoToCiboDao by lazy { internalDatabaseRef.pastoToCiboDao() }
    private val pastoDao by lazy { internalDatabaseRef.pastoDao() }
    private val sportDao by lazy { internalDatabaseRef.sportDao() }
    private val attivitaDao by lazy { internalDatabaseRef.attivitaDao() }
    val internalFileData by lazy { File(filesDir, "internalData") }


    var userData: UserDataViewModel? = null

    //add database repositories
    val userRepository by lazy { UserRepository(userDao) }
    val alimentoRepository by lazy { AlimentoRepository(alimentoDao) }
    val pastoRepository by lazy { PastoRepository(pastoDao) }
    val pastoToCiboRepository by lazy { PastoToCiboRepository(pastoToCiboDao) }
    val sportRepository by lazy { SportRepository(sportDao) }
    val attivitaRepository by lazy { AttivitaRepository(attivitaDao) }

    //Firebase Setup

    private val firebaseDatabaseRef by lazy {
        FirebaseDatabase.getInstance("https://my-free-health-tracker-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference()
    }

    fun getFirebaseDatabaseRef(firebaseDBTable: FirebaseDBTable): DatabaseReference {
        return firebaseDatabaseRef.child(firebaseDBTable.toString())
    }


    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val counterChannel = NotificationChannel(
                "counter_channel",
                "Counter Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(counterChannel)
            }
        }

    }
    override fun onTerminate() {
        internalDatabaseRef.close()
        super.onTerminate()

    }
}

enum class FirebaseDBTable(val description: String) {
    USERS("users"),
    ALIMENTI("alimenti"),
    PASTO("pasto"),
    SPORT("sport"),
    ATTIVITA("attivita");

    override fun toString(): String {
        return description
    }

}
