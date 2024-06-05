package com.example.myfreehealthtracker.LocalDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.LocalDatabase.Entities.PastoToCibo
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface PastoToCiboDao {


    @Upsert
    suspend fun insertPastoToCibo(pastoToCibo: PastoToCibo)

    @Query("SELECT * FROM PastoToCibo ")
    fun getAllPastoToCibo(): Flow<List<PastoToCibo>>


    @Query("SELECT * FROM Alimento WHERE Alimento.id IN (SELECT idAlimentoId FROM PastoToCibo WHERE idUserID =:userId AND idDate=:date)")
    fun getAlimentiByPasto(userId: String, date: Date): Flow<List<Alimento>>

    @Query("SELECT quantity FROM PastoToCibo WHERE idUserID =:userId AND idDate=:date AND idAlimentoId =:idAlimento")
    suspend fun getQuantitaByPasto(userId: String, date: Date, idAlimento: String): Float



}