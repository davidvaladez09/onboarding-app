package com.example.onboarding.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onboarding.data.entities.User
import com.example.onboarding.data.repositories.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun login(username: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUser(username, password)
            onResult(user)
        }
    }

    fun register(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val existingUser = repository.getUserByUsername(user.username)
                if (existingUser != null) {
                    onResult(false)
                } else {
                    repository.insertUser(user)
                    onResult(true)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}