package com.example.myfreehealthtracker.LocalDatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.LocalDatabase.Daos.PastoDao
import com.example.myfreehealthtracker.LocalDatabase.Entities.Pasto
import kotlinx.coroutines.flow.Flow
import java.util.Date

class PastoRepository(private val pastoDao: PastoDao) {

    val allPasto: Flow<List<Pasto>> = pastoDao.getPasto()

    @WorkerThread
    suspend fun insertPasto(pasto: Pasto) {
        pastoDao.insertPasto(pasto)
    }

    @WorkerThread
    fun getPastoByDate(date: Date) = pastoDao.getPastoByDate(date)

}