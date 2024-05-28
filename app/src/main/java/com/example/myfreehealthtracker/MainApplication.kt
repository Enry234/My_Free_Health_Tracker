package com.example.myfreehealthtracker

import android.app.Application
import com.example.myfreehealthtracker.LocalDatabase.Entities.UserData
import com.example.myfreehealthtracker.LocalDatabase.InternalDatabase
import com.example.myfreehealthtracker.LocalDatabase.Repositories.UserRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.io.File

class MainApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val firebaseDatabaseRef by lazy {
        FirebaseDatabase.getInstance("https://my-free-health-tracker-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference().child("users")
    }
    val internalDatabaseRef by lazy { InternalDatabase.getDatabase(this) }
    val userDao by lazy { internalDatabaseRef.userDao() }
    val alimentoDao by lazy { internalDatabaseRef.alimentoDao() }

    val internalRepository by lazy { UserRepository(internalDatabaseRef.userDao()) }
    val internalFileData by lazy { File(filesDir, "internalData") }
    var userData: UserData? = null

}
