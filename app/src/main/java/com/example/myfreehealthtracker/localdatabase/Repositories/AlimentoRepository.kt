package com.example.myfreehealthtracker.localdatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.localdatabase.Daos.AlimentoDao
import com.example.myfreehealthtracker.localdatabase.Entities.Alimento
import kotlinx.coroutines.flow.Flow

class AlimentoRepository(private val alimentoDao: AlimentoDao) {

    val allAlimento: Flow<List<Alimento>> = alimentoDao.getAllAlimento()

    @WorkerThread
    suspend fun insertAlimento(alimento: Alimento){
        alimentoDao.insertAlimento(alimento)
    }

    @WorkerThread
    fun getAlimentoById(barcode: String) = alimentoDao.getAlimentoById(barcode)


}