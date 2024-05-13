package com.example.myfreehealthtracker

import android.app.Application
import com.example.myfreehealthtracker.LocalDatabase.InternalDatabase
import com.example.myfreehealthtracker.LocalDatabase.Repository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val firebaseDatabaseRef by lazy { FirebaseDatabase.getInstance().getReference("User") }
    val internalDatabaseRef by lazy { InternalDatabase.getDatabase(this) }
    val internalRepository by lazy { Repository(internalDatabaseRef.userDao()) }
}