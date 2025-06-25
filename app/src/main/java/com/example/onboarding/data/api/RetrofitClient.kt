package com.example.onboarding.data.api

import com.example.onboarding.core.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: RickAndMortyService by lazy {
        retrofit.create(RickAndMortyService::class.java)
    }
}