package com.example.onboarding.data.api

import com.example.onboarding.data.dto.CharacterResponse
import retrofit2.Response
import retrofit2.http.GET

interface RickAndMortyService {
    @GET("character")
    suspend fun getCharacters(): Response<CharacterResponse>
}