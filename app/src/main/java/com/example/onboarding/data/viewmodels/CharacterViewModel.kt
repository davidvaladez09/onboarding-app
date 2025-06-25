package com.example.onboarding.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onboarding.data.dto.Character
import com.example.onboarding.data.repositories.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class CharacterViewModel : ViewModel() {
    private lateinit var repository: CharacterRepository
    private val _allCharacters = MutableStateFlow<List<Character>>(emptyList())
    private val _filteredCharacters = MutableStateFlow<List<Character>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val filteredCharacters: StateFlow<List<Character>> = _filteredCharacters.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setRepository(repository: CharacterRepository) {
        this.repository = repository
    }

    fun loadCharacters() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                _allCharacters.value = repository.getCharacters()
                _filteredCharacters.value = _allCharacters.value
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading characters"
                _filteredCharacters.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterCharacters(query: String) {
        val filteredList = if (query.isEmpty()) {
            _allCharacters.value
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            _allCharacters.value.filter { character ->
                character.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        character.status.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        character.species.lowercase(Locale.getDefault()).contains(lowerCaseQuery)
            }
        }
        _filteredCharacters.value = filteredList
    }

    fun clearError() {
        _error.value = null
    }
}