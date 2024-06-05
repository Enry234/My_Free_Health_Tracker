package com.example.myfreehealthtracker.LocalDatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.LocalDatabase.Daos.SportDao
import com.example.myfreehealthtracker.LocalDatabase.Entities.Sport
import kotlinx.coroutines.flow.Flow

class SportRepository(private val sportDao: SportDao) {

    val allSports: Flow<List<Sport>> = sportDao.getAllSports()

    @WorkerThread
    suspend fun insertSport(sport: Sport) {
        sportDao.insertSport(sport)
    }

    @WorkerThread
    suspend fun getSportById(id: String) {
        sportDao.getSportById(id)
    }

    @WorkerThread
    suspend fun deleteSport(sport: Sport) {
        sportDao.deleteSport(sport)
    }


}