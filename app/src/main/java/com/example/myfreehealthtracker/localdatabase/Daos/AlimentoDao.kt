package com.example.myfreehealthtracker.localdatabase.Daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myfreehealthtracker.localdatabase.Entities.Alimento
import kotlinx.coroutines.flow.Flow


@Dao
interface AlimentoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlimento(alimento: Alimento)


    @Query("SELECT * FROM Alimento ORDER BY nome DESC")
    fun getAllAlimento(): Flow<List<Alimento>>


    @Query("SELECT * FROM Alimento WHERE id = :barcode")
    fun getAlimentoById(barcode: String): Flow<Alimento>

    @Query("DELETE FROM Alimento")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteAlimento(alimento: Alimento)
}