package com.example.myfreehealthtracker.localdatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.localdatabase.Daos.PastoToCiboDao
import com.example.myfreehealthtracker.localdatabase.Entities.Alimento
import com.example.myfreehealthtracker.localdatabase.Entities.Pasto
import com.example.myfreehealthtracker.localdatabase.Entities.PastoToCibo

class PastoToCiboRepository(private val pastoToCiboDao: PastoToCiboDao) {



    @WorkerThread
    suspend fun insertPastoToCibo(pastoToCibo: PastoToCibo) {
        pastoToCiboDao.insertPastoToCibo(pastoToCibo)
    }

    val allPastoToCibo = pastoToCiboDao.allPastoToCibo()


    @WorkerThread
    fun getAlimentiByPasto(pasto: Pasto) = pastoToCiboDao.getAlimentiByPasto(pasto.userID, pasto.date)

    @WorkerThread
    fun getQuantitaByPasto(pasto: Pasto, alimento: Alimento) =
        pastoToCiboDao.getQuantitaByPasto(pasto.userID, pasto.date, alimento.id)

    @WorkerThread
    fun getAllQuantities() = pastoToCiboDao.getAllQuantities()


}