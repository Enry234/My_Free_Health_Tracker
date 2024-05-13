package com.example.myfreehealthtracker.LocalDatabase

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myfreehealthtracker.Models.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun insertUser(user: UserData)

    @Query("SELECT * FROM User")
    fun getUser(): Flow<List<UserData>>

}