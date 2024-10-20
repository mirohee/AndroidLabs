package com.example.lab16parliament.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.lab16parliament.network.MPApiRetrofit
import com.example.lab16parliament.repository.MPRepository
import com.example.lab16parliament.repository.ParliamentDatabase

/**
 * Miro Saarinen
 * 21/10/2024
 * Worker class to update data in the Room database.
 */
class MPDataUpdateWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            // Initialize Room database and repository
            val mpDao = ParliamentDatabase.getInstance(applicationContext).mpDao()
            val mpRepository = MPRepository(mpDao)

            // Refresh data by calling the repository's method
            mpRepository.refreshMPs()

            Result.success()
        } catch (exception: Exception) {
            Log.e("MPDataUpdateWorker", "Error updating MP data", exception)
            Result.failure()
        }
    }
}