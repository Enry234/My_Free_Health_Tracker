package com.example.myfreehealthtracker.LocalDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myfreehealthtracker.LocalDatabase.Entities.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun insertUser(user: UserData): Long

    @Query("SELECT * FROM User LIMIT 1")
    fun getAllUser(): Flow<List<UserData>>




    //@Upsert
    //suspend fun insertAssunzione(assunzione: Assunzione)

    //@Upsert
    //suspend fun insertAttivita(attivita: Attivita)

    //@Upsert
    //suspend fun insertMedicine(medicine: Medicine)

    //@Upsert
    //suspend fun insertSport(sport: Sport)


    //@Query("SELECT * FROM Assunzione")
    //fun getAssunzione(): Flow<List<Assunzione>>


    //@Query("SELECT * FROM Attivita")
    //fun getAttivita(): Flow<List<Attivita>>


    //@Query("SELECT * FROM Medicine")
    //fun getMedicine(): Flow<List<Medicine>>


    //@Query("SELECT * FROM Sport")
    //fun getSport(): Flow<List<Sport>>

}