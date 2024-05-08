package com.example.myfreehealthtracker.Models

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface UserDao {
    @Upsert
    suspend fun insertAll(vararg user: UserData)


}