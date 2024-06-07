package com.example.myfreehealthtracker.LocalDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myfreehealthtracker.LocalDatabase.Daos.AlimentoDao
import com.example.myfreehealthtracker.LocalDatabase.Daos.AttivitaDao
import com.example.myfreehealthtracker.LocalDatabase.Daos.PastoDao
import com.example.myfreehealthtracker.LocalDatabase.Daos.PastoToCiboDao
import com.example.myfreehealthtracker.LocalDatabase.Daos.SportDao
import com.example.myfreehealthtracker.LocalDatabase.Daos.UserDao
import com.example.myfreehealthtracker.LocalDatabase.Entities.Alimento
import com.example.myfreehealthtracker.LocalDatabase.Entities.Attivita
import com.example.myfreehealthtracker.LocalDatabase.Entities.Pasto
import com.example.myfreehealthtracker.LocalDatabase.Entities.PastoToCibo
import com.example.myfreehealthtracker.LocalDatabase.Entities.Sport
import com.example.myfreehealthtracker.LocalDatabase.Entities.UserData

@Database(
    entities = [Alimento::class, Attivita::class, Pasto::class, PastoToCibo::class, Sport::class, UserData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(InternalConverter::class)
abstract class InternalDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun alimentoDao(): AlimentoDao

    abstract fun pastoDao(): PastoDao

    abstract fun pastoToCiboDao(): PastoToCiboDao
    abstract fun sportDao(): SportDao

    abstract fun attivitaDao(): AttivitaDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: InternalDatabase? = null

        fun getDatabase(context: Context): InternalDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InternalDatabase::class.java,
                    "main_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }


}