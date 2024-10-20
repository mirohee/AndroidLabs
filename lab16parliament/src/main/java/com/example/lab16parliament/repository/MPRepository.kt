package com.example.lab16parliament.repository

import com.example.lab16parliament.data.MP
import com.example.lab16parliament.data.MPComment
import com.example.lab16parliament.data.MPExtras
import com.example.lab16parliament.network.MPApiRetrofit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Miro Saarinen
 * 21/10/2024
 * Repository for MPs.
 */
class MPRepository(private val mpDao: MPDao) {

    fun getMPById(hetekaId: Int): Flow<MP> = mpDao.getMPById(hetekaId)

    fun getMPsByParty(party: String): Flow<List<MP>> = mpDao.getMPsByParty(party)

    fun getParties(): Flow<List<String>> = mpDao.getParties()

    fun getMPExtras(hetekaId: Int): Flow<List<MPExtras>> = mpDao.getMPExtras(hetekaId)

    fun getMPComments(hetekaId: Int): Flow<List<MPComment>> = mpDao.getMPComments(hetekaId)

    // Insert all MPs into the local database
    suspend fun insertAll(members: List<MP>) {
        mpDao.insertAll(members)
    }

    // Insert extra data for an MP into the local database
    suspend fun insertExtras(mpExtras: List<MPExtras>) {
        mpDao.insertExtras(mpExtras)
    }

    // Add a comment and grade to the database
    suspend fun addCommentAndGrade(mpCommentAndGrade: MPComment) {
        mpDao.insertCommentAndGrade(mpCommentAndGrade)
    }

    // Fetch MPs from the network and save them to the local database
    suspend fun refreshMPs() {

        // Use coroutine to fetch data from the network
        withContext(Dispatchers.IO) {
            val mpList = MPApiRetrofit.retrofitService.getMPsData()
            insertAll(mpList) // Save the fetched MPs to the local database

            // Fetch extra data for each MP
            mpList.forEach { mp ->
                val mpExtras = MPApiRetrofit.retrofitService.getMPsExtraData()
                insertExtras(mpExtras)
            }
        }
    }
}
