package com.example.myfreehealthtracker.LocalDatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.LocalDatabase.Daos.PastoToCiboDao
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.LocalDatabase.Entities.Pasto
import com.example.myfreehealthtracker.LocalDatabase.Entities.PastoToCibo

class PastoToCiboRepo(private val pastoToCiboDao: PastoToCiboDao) {



    @WorkerThread
    suspend fun insertPastoToCibo(pastoToCibo: PastoToCibo) {
        pastoToCiboDao.insertPastoToCibo(pastoToCibo)
    }

    @WorkerThread
    fun getAlimentiByPasto(pasto: Pasto) = pastoToCiboDao.getAlimentiByPasto(pasto.userID, pasto.date)

    @WorkerThread
    suspend fun getQuantitaByPasto(pasto:Pasto, alimento: Alimento) = pastoToCiboDao.getQuantitaByPasto(pasto.userID, pasto.date, alimento.id )

}