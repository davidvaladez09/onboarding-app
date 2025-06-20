package com.example.onboarding.data.repositories

import com.example.onboarding.data.dao.UserDao
import com.example.onboarding.data.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun getUserByCredentials(email: String, password: String) = withContext(Dispatchers.IO) {
        userDao.getUserByCredentials(email, password)
    }

    suspend fun getUserByEmail(email: String) = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    suspend fun getUser(username: String, password: String) =
        userDao.getUser(username, password)

    suspend fun getUserByUsername(username: String) =
        userDao.getUserByUsername(username)
}