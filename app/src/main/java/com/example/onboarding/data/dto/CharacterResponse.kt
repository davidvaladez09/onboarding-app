package com.example.onboarding.data.dto

import java.io.Serializable
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class CharacterResponse(
    val info: Info,
    val results: List<Character>
)

data class Info(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Origin,
    val location: Location,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
) : Serializable

data class Origin(
    val name: String,
    val url: String
) : Serializable

data class Location(
    val name: String,
    val url: String
) : Serializable

object PasswordUtils {
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 32

    fun generateSalt(): ByteArray {
        val secureRandom = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        secureRandom.nextBytes(salt)
        return salt
    }

    fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val keySpec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return keyFactory.generateSecret(keySpec).encoded
    }

    fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = hexArray[v ushr 4]
            hexChars[i * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    fun hexToBytes(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((hex[i].digitToInt(16) shl 4) +
                    hex[i + 1].digitToInt(16)).toByte()
            i += 2
        }
        return data
    }
}