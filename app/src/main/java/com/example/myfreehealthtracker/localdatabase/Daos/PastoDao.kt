package com.example.myfreehealthtracker.localdatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myfreehealthtracker.localdatabase.Entities.Pasto
import kotlinx.coroutines.flow.Flow
import java.util.Date


@Dao
interface PastoDao {

    @Upsert
    suspend fun insertPasto(pasto: Pasto)


    @Query("SELECT * FROM Pasto")
    fun getPasto(): Flow<List<Pasto>>

    @Query("SELECT * FROM Pasto WHERE date = :date")
    fun getPastoByDate(date: Date): Flow<List<Pasto>>


}