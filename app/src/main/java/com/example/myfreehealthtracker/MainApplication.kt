package com.example.myfreehealthtracker

import android.app.Application
import com.example.myfreehealthtracker.LocalDatabase.InternalDatabase
import com.example.myfreehealthtracker.LocalDatabase.Repository
import com.example.myfreehealthtracker.Models.UserData
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.io.File

class MainApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val firebaseDatabaseRef by lazy { FirebaseDatabase.getInstance().getReference("User") }
    val internalDatabaseRef by lazy { InternalDatabase.getDatabase(this) }
    val userDao by lazy { internalDatabaseRef.userDao() }
    val internalRepository by lazy { Repository(internalDatabaseRef.userDao()) }
    val internalFileData by lazy { File(filesDir, "internalData") }
    var userData: UserData? = null
}
