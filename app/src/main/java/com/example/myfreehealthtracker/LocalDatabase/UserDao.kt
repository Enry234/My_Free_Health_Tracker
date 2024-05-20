package com.example.myfreehealthtracker.LocalDatabase

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myfreehealthtracker.Models.Alimento
import com.example.myfreehealthtracker.Models.Assunzione
import com.example.myfreehealthtracker.Models.Attivita
import com.example.myfreehealthtracker.Models.Medicine
import com.example.myfreehealthtracker.Models.Pasto
import com.example.myfreehealthtracker.Models.PastoToCibo
import com.example.myfreehealthtracker.Models.Sport
import com.example.myfreehealthtracker.Models.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun insertUser(user: UserData)

    @Upsert
    suspend fun insertPasto(pasto: Pasto)

    @Upsert
    suspend fun insertPastoToCibo(pastoToCibo: PastoToCibo)

    @Upsert
    suspend fun insertAlimento(alimento: Alimento)

    @Upsert
    suspend fun insertAssunzione(assunzione: Assunzione)

    @Upsert
    suspend fun insertAttivita(attivita: Attivita)

    @Upsert
    suspend fun insertMedicine(medicine: Medicine)

    @Upsert
    suspend fun insertSport(sport: Sport)


    @Query("SELECT * FROM User")
    fun getUser(): Flow<List<UserData>>


    @Query("SELECT * FROM Alimento")
    fun getAlimenti(): Flow<List<Alimento>>


    @Query("SELECT * FROM Assunzione")
    fun getAssunzione(): Flow<List<Assunzione>>


    @Query("SELECT * FROM Attivita")
    fun getAttivita(): Flow<List<Attivita>>


    @Query("SELECT * FROM Medicine")
    fun getMedicine(): Flow<List<Medicine>>


    @Query("SELECT * FROM Pasto")
    fun getPastoToCibo(): Flow<List<PastoToCibo>>


    @Query("SELECT * FROM Sport")
    fun getSport(): Flow<List<Sport>>

}