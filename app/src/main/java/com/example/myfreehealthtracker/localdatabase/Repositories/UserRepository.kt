package com.example.myfreehealthtracker.localdatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.localdatabase.Daos.UserDao
import com.example.myfreehealthtracker.localdatabase.Entities.UserData
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allUser: Flow<UserData> = userDao.getAllUser()

    // By default Room runs suspend queries off the main thread. We don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insertUser(user: UserData): Long {
        return userDao.insertUser(user)
    }
}
