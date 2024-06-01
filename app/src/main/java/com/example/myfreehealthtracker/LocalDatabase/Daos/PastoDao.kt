package com.example.myfreehealthtracker.LocalDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myfreehealthtracker.LocalDatabase.Entities.Pasto
import kotlinx.coroutines.flow.Flow


@Dao
interface PastoDao {

    @Upsert
    suspend fun insertPasto(pasto: Pasto)


    @Query("SELECT * FROM Pasto")
    fun getPasto(): Flow<List<Pasto>>



}