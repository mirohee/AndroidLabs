package com.example.lab16parliament.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Miro Saarinen
 * 21/10/2024
 * Retrofit service for fetching MPs from the network.
 */

object MPApiRetrofit {
    private const val BASE_URL = "https://users.metropolia.fi/"

    val retrofitService: MPApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Use GsonConverterFactory for JSON
            .build()
            .create(MPApiService::class.java)
    }
}