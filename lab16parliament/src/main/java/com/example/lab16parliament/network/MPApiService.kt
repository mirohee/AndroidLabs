package com.example.lab16parliament.network

import com.example.lab16parliament.data.MP
import com.example.lab16parliament.data.MPExtras
import retrofit2.http.GET

/**
 * Miro Saarinen
 * 21/10/2024
 * Retrofit service for fetching MPs from the network.
 */

interface MPApiService {
    @GET("~peterh/seating.json")
    suspend fun getMPsData(): List<MP>

    @GET("~peterh/extras.json")
    suspend fun getMPsExtraData(): List<MPExtras>

}