package com.example.myfreehealthtracker.LocalDatabase.Daos

import androidx.room.Dao
import androidx.room.Upsert
import com.example.myfreehealthtracker.LocalDatabase.Entities.PastoToCibo

@Dao
interface PastoToCiboDao {


    @Upsert
    suspend fun insertPastoToCibo(pastoToCibo: PastoToCibo)


    //@Query("SELECT * FROM Pasto ORDER BY date DESC")
    //fun getPasto(): Flow<List<Pasto>>



}