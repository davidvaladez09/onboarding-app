package com.example.onboarding.data.repositories

import com.example.onboarding.data.dto.Character
import com.example.onboarding.data.api.RetrofitClient

class CharacterRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getCharacters(): List<Character> {
        return try {
            val response = apiService.getCharacters()
            if (response.isSuccessful) {
                response.body()?.results ?: throw Exception("Empty response body")
            } else {
                throw Exception("API call failed: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to fetch characters: ${e.message}")
        }
    }
}