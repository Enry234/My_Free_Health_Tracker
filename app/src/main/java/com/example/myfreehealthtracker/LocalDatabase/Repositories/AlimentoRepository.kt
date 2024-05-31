package com.example.myfreehealthtracker.LocalDatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.LocalDatabase.Daos.AlimentoDao
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import kotlinx.coroutines.flow.Flow

class AlimentoRepository(private val AlimentoDao: AlimentoDao) {

    val allAlimento: Flow<List<Alimento>> = AlimentoDao.getAllAlimento()

    @WorkerThread
    suspend fun insertAlimento(alimento: Alimento){
        AlimentoDao.insertAlimento(alimento)
    }

    @WorkerThread
    suspend fun getAlimentoById(barcode: String){
        AlimentoDao.getAlimentoById(barcode)
    }

    @WorkerThread
    suspend fun deleteAlimento(alimento: Alimento){
        AlimentoDao.deleteAlimento(alimento)
    }



}