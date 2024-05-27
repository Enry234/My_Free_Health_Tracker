package com.example.myfreehealthtracker.ViewModel

import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myfreehealthtracker.LocalDatabase.InternalConverter
import com.example.myfreehealthtracker.LocalDatabase.InternalDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class InternalConverterTest {

    private lateinit var db: InternalDatabase
    private lateinit var internalConverter: InternalConverter

    @Before
    fun setup() {
        db = inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            InternalDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun dateToString_nullDate_returnsEmptyString() {
        val date: Date? = null
        val expected = ""

        val actual = internalConverter.DateToString(date)

        assertEquals(expected, actual)
    }

    @Test
    fun dateToString_validDate_returnsStringRepresentation() {
        val date = Date()
        val expected = date.toString()

        val actual = internalConverter.DateToString(date)

        assertEquals(expected, actual)
    }

    @Test
    fun stringToDate_emptyString_returnsCurrentDate() {
        val dateString = ""
        val expected = Date()

        val actual = internalConverter.StringToDate(dateString)

        assertEquals(expected, actual)
    }

    @Test
    fun stringToDate_validString_returnsDateObject() {
        val dateString = "2023-04-01"
        val expected = Date(dateString)

        val actual = internalConverter.StringToDate(dateString)

        assertEquals(expected, actual)
    }

    // Add similar tests for the other converter methods (listPairStringIntToString, etc.)
}