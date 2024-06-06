package com.example.myfreehealthtracker.LocalDatabase.Repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import com.example.myfreehealthtracker.LocalDatabase.Daos.PastoDao
import com.example.myfreehealthtracker.LocalDatabase.Entities.Pasto
import java.util.Date

class PastoRepository(private val pastoDao: PastoDao) {

    @WorkerThread
    fun allPasto() = pastoDao.getPasto().asLiveData()

    @WorkerThread
    suspend fun insertPasto(pasto: Pasto) {
        pastoDao.insertPasto(pasto)
    }

    @WorkerThread
    fun getPastoByDate(date: Date) = pastoDao.getPastoByDate(date)

}