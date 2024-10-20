package com.example.lab16parliament

import android.app.Application
import com.example.lab16parliament.repository.ParliamentDatabase

/**
 * Miro Saarinen
 * 21/10/2024
 * Application class to initialize the database.
 */
class ParliamentApp: Application() {
    lateinit var database: ParliamentDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = ParliamentDatabase.getInstance(this)
    }
}