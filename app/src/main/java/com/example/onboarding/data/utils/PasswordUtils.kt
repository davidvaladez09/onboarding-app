package com.example.onboarding.data.utils

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordUtils {
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest("${salt}${password}".toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, salt: String, storedHash: String): Boolean {
        return hashPassword(password, salt) == storedHash
    }
}