package com.example.lab16parliament

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lab16parliament.ui.MPNavHost
import com.example.lab16parliament.ui.theme.AndroidsensorlabsTheme
import com.example.lab16parliament.repository.MPRepository
import com.example.lab16parliament.workmanager.MPDataUpdateWorker
import java.util.concurrent.TimeUnit


/**
 * Miro Saarinen
 * 21/10/2024
 * Main activity that starts the app and setups the database and work manager.
 */
class MainActivity : ComponentActivity() {
    private lateinit var repository: MPRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = (application as ParliamentApp).database
        val mpDao = database.mpDao()
        repository = MPRepository(mpDao)

        setContent {
            AndroidsensorlabsTheme {
                MPNavHost(repository = repository)
            }
        }

        // Setup the work manager
        val workRequest = PeriodicWorkRequestBuilder<MPDataUpdateWorker>(6, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .build()

        // Enqueue the work request
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "MPDataUpdateWork",
            ExistingPeriodicWorkPolicy.KEEP, // Keeps the previous work if it's already scheduled
            workRequest
        )
    }
}
