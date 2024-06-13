package com.example.myfreehealthtracker.localdatabase.Daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myfreehealthtracker.localdatabase.Entities.Attivita
import kotlinx.coroutines.flow.Flow

@Dao
interface AttivitaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAttivita(attivita: Attivita)


    @Query("SELECT * FROM Attivita")
    fun getAllAttivita(): Flow<List<Attivita>>


    @Query("DELETE FROM Attivita")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteAttivita(attivita: Attivita)
}