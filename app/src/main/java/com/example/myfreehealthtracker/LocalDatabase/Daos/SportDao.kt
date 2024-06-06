package com.example.myfreehealthtracker.LocalDatabase.Daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myfreehealthtracker.LocalDatabase.Entities.Sport
import kotlinx.coroutines.flow.Flow
@Dao
interface SportDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSport(sport: Sport)


    @Query("SELECT * FROM Sport")
    fun getAllSports(): Flow<List<Sport>>


    @Query("SELECT * FROM Sport WHERE id = :id")
    fun getSportById(id: String): Flow<Sport>

    @Query("DELETE FROM Sport")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteSport(sport: Sport)
}