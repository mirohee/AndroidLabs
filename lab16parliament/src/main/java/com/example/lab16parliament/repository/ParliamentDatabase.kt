package com.example.lab16parliament.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lab16parliament.data.MP
import com.example.lab16parliament.data.MPComment
import com.example.lab16parliament.data.MPExtras


/**
 * Miro Saarinen
 * 21/10/2024
 * Room database for the Parliament app.
 */
@Database(entities = [MP::class, MPComment::class, MPExtras::class], version = 2)
abstract class ParliamentDatabase : RoomDatabase() {
    abstract fun mpDao(): MPDao

    companion object {
        private const val DATABASE_NAME = "parliament_db"

        @Volatile
        private var INSTANCE: ParliamentDatabase? = null

        fun getInstance(context: Context): ParliamentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParliamentDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
