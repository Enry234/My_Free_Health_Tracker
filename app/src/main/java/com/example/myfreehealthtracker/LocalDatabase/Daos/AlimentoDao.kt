package com.example.myfreehealthtracker.LocalDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import kotlinx.coroutines.flow.Flow


@Dao
interface AlimentoDao {
    @Upsert
    suspend fun insertAlimento(alimento: Alimento)


    @Query("SELECT * FROM Alimento ORDER BY nome DESC")
    fun getAllAlimento(): Flow<List<Alimento>>


    @Query("SELECT * FROM Alimento WHERE id = :barcode")
    fun getAlimentoById(barcode: String): Flow<Alimento>

}