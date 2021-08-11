package com.openclassrooms.realestatemanager.helpers

import okhttp3.OkHttpClient
import retrofit2.Retrofit


object ApiClient {
    private const val BASE_URL: String = "https://maps.googleapis.com/maps/api/"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
