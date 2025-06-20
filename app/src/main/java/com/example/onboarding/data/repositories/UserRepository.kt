package com.example.onboarding.data.repositories

import com.example.onboarding.data.dao.UserDao
import com.example.onboarding.data.entities.User
import com.example.onboarding.data.utils.PasswordUtils
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

    suspend fun emailExists(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }

    suspend fun registerUser(name: String, email: String, password: String): Long {
        val salt = PasswordUtils.generateSalt()
        val hashedPassword = PasswordUtils.hashPassword(password, salt)

        val user = User(
            username = name,
            email = email,
            password = PasswordUtils.bytesToHex(hashedPassword),
            salt = PasswordUtils.bytesToHex(salt)
        )

        return userDao.insertUser(user)
    }
}