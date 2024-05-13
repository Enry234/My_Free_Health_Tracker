package com.example.myfreehealthtracker.LocalDatabase

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.Models.UserData
import kotlinx.coroutines.flow.Flow

class Repository(private val userDao: UserDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val user: Flow<List<UserData>> = userDao.getUser()

    // By default Room runs suspend queries off the main thread. We don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(user: UserData) {
        userDao.insertUser(user)
    }
}
