package com.example.myfreehealthtracker.LocalDatabase.Repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.myfreehealthtracker.LocalDatabase.Daos.PastoDao
import com.example.myfreehealthtracker.LocalDatabase.Entities.Pasto

class PastoRepository(private val pastoDao: PastoDao) {

    val allPasto: LiveData<List<Pasto>> = pastoDao.getPasto().asLiveData()
    @WorkerThread
    suspend fun insertPasto(pasto: Pasto) {
        pastoDao.insertPasto(pasto)
    }

}