package com.example.myfreehealthtracker.LocalDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myfreehealthtracker.Models.Alimento
import com.example.myfreehealthtracker.Models.Assunzione
import com.example.myfreehealthtracker.Models.Attivita
import com.example.myfreehealthtracker.Models.Medicine
import com.example.myfreehealthtracker.Models.Pasto
import com.example.myfreehealthtracker.Models.PastoToCibo
import com.example.myfreehealthtracker.Models.Sport
import com.example.myfreehealthtracker.Models.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [Alimento::class, Assunzione::class, Attivita::class, Medicine::class, Pasto::class, PastoToCibo::class, Sport::class, UserData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(InternalConverter::class)
abstract class InternalDatabase : RoomDatabase() {

    public abstract fun userDao(): UserDao


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: InternalDatabase? = null

        @OptIn(DelicateCoroutinesApi::class)
        fun getDatabase(context: Context): InternalDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InternalDatabase::class.java,
                    "word_database"
                ).addCallback(UserDatabaseCallback(GlobalScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class UserDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // If you want to keep the data through app restarts,
            // comment out the following line.
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.userDao())
                }
            }
        }

        suspend fun populateDatabase(userDao: UserDao) {


//            // Add sample words.
//            val user = UserData(
//                "nome", "cognome", "asc123", Date("11/11/2022"), 'm', 180, null,
//                mutableListOf(Pair<Date, Int>(Date(), 80)), null
//            )
//            userDao.insertUser(user)
        }
    }
}