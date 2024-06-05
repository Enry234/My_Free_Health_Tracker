package com.example.myfreehealthtracker.LocalDatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.LocalDatabase.Daos.AlimentoDao
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import kotlinx.coroutines.flow.Flow

class AlimentoRepository(private val alimentoDao: AlimentoDao) {

    val allAlimento: Flow<List<Alimento>> = alimentoDao.getAllAlimento()

    @WorkerThread
    suspend fun insertAlimento(alimento: Alimento){
        alimentoDao.insertAlimento(alimento)
    }

    @WorkerThread
    suspend fun getAlimentoById(barcode: String) = alimentoDao.getAlimentoById(barcode)

    @WorkerThread
    suspend fun deleteAlimento(alimento: Alimento){
        alimentoDao.deleteAlimento(alimento)
    }



}