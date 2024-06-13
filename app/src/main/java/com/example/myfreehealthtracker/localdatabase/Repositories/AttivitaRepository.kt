package com.example.myfreehealthtracker.LocalDatabase.Repositories

import androidx.annotation.WorkerThread
import com.example.myfreehealthtracker.LocalDatabase.Daos.AttivitaDao
import com.example.myfreehealthtracker.LocalDatabase.Entities.Attivita
import kotlinx.coroutines.flow.Flow

class AttivitaRepository(private val attivitaDao: AttivitaDao) {
    val allAttivita: Flow<List<Attivita>> = attivitaDao.getAllAttivita()

    @WorkerThread
    suspend fun insertAttivita(attivita: Attivita) {
        attivitaDao.insertAttivita(attivita)
    }

    @WorkerThread
    suspend fun deleteSport(attivita: Attivita) {
        attivitaDao.deleteAttivita(attivita)
    }
}