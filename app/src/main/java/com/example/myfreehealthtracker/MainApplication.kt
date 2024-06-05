package com.example.myfreehealthtracker

import android.app.Application
import com.example.myfreehealthtracker.LocalDatabase.InternalDatabase
import com.example.myfreehealthtracker.LocalDatabase.Repositories.AlimentoRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.AttivitaRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.PastoRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.PastoToCiboRepo
import com.example.myfreehealthtracker.LocalDatabase.Repositories.SportRepository
import com.example.myfreehealthtracker.LocalDatabase.Repositories.UserRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.io.File

class MainApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    private val firebaseDatabaseRef by lazy {
        FirebaseDatabase.getInstance("https://my-free-health-tracker-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference()
    }


    val internalDatabaseRef by lazy { InternalDatabase.getDatabase(this) }
    val userDao by lazy { internalDatabaseRef.userDao() }
    val alimentoDao by lazy { internalDatabaseRef.alimentoDao() }
    val pastoToCiboDao by lazy { internalDatabaseRef.pastoToCiboDao() }
    val pastoDao by lazy { internalDatabaseRef.pastoDao() }
    val sportDao by lazy { internalDatabaseRef.sportDao() }
    val attivitaDao by lazy { internalDatabaseRef.attivitaDao() }
    val internalRepository by lazy { UserRepository(internalDatabaseRef.userDao()) }
    val internalFileData by lazy { File(filesDir, "internalData") }


    var userData: UserDataViewModel? = null

    //add database repositories
    val userRepo by lazy { UserRepository(userDao) }
    val alimentoRepo by lazy { AlimentoRepository(alimentoDao) }
    val pastoRepo by lazy { PastoRepository(pastoDao) }
    val pastoToCiboRepo by lazy { PastoToCiboRepo(pastoToCiboDao) }
    val sportRepo by lazy {
        SportRepository(sportDao)
    }
    val attivitaRepo by lazy {
        AttivitaRepository(attivitaDao)
    }


    fun getFirebaseDatabaseRef(firebaseDBTable: FirebaseDBTable): DatabaseReference {
        return firebaseDatabaseRef.child(firebaseDBTable.toString())
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
