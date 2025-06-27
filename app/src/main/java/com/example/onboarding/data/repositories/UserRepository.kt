package com.example.onboarding.data.repositories

import com.example.onboarding.data.dao.UserDao
import com.example.onboarding.data.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun getUserByEmail(email: String) = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    suspend fun emailExists(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }
}