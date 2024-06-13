package com.example.myfreehealthtracker.localdatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.localdatabase.Daos.SportDao
import com.example.myfreehealthtracker.localdatabase.Entities.Sport
import kotlinx.coroutines.flow.Flow

class SportRepository(private val sportDao: SportDao) {

    val allSports: Flow<List<Sport>> = sportDao.getAllSports()

    @WorkerThread
    suspend fun insertSport(sport: Sport) {
        sportDao.insertSport(sport)
    }

    @WorkerThread
    fun getSportById(id: String) = sportDao.getSportById(id)


    @WorkerThread
    suspend fun deleteSport(sport: Sport) {
        sportDao.deleteSport(sport)
    }


}