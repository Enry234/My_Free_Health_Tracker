package com.example.myfreehealthtracker.LocalDatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.LocalDatabase.Daos.PastoToCiboDao
import com.example.myfreehealthtracker.LocalDatabase.Entities.PastoToCibo

class PastoToCiboRepo(private val pastoToCiboDao: PastoToCiboDao) {



    @WorkerThread
    suspend fun insertPastoToCibo(pastoToCibo: PastoToCibo) {
        pastoToCiboDao.insertPastoToCibo(pastoToCibo)
    }

    @WorkerThread
    suspend fun getAlimentiByPasto(pastoToCibo: PastoToCibo) {
        pastoToCiboDao.getAlimentiByPasto(pastoToCibo.userID, pastoToCibo.date) //VERY DANGEROUS ;)
    }

}