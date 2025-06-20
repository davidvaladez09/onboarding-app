package com.example.onboarding.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.onboarding.R
import com.example.onboarding.data.database.AppDatabase
import com.example.onboarding.data.entities.User
import com.example.onboarding.data.repositories.UserRepository
import com.example.onboarding.data.utils.PasswordUtils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val tag = "LoginActivity"

    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        val database = AppDatabase.getDatabase(this)
        repository = UserRepository(database.userDao())

        addTestUser()

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.loginButton)
        val btnRegister = findViewById<Button>(R.id.registerButton)

        btnRegister.setOnClickListener {
            navigateToRegister()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()


            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = repository.getUserByEmail(email)

                    runOnUiThread {
                        if (user != null) {
                            val hashedInput = PasswordUtils.hashPassword(
                                password,
                                PasswordUtils.hexToBytes(user.salt)
                            )

                            if (PasswordUtils.bytesToHex(hashedInput) == user.password) {
                                navigateToHome()
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Incorrect email or password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Incorrect email or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.message
                }
            }
        }
    }

    private fun addTestUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val testEmail = "test@example.com"
                if (repository.getUserByEmail(testEmail) == null) {
                    val testPassword = "123456"
                    val salt = PasswordUtils.generateSalt()
                    val hashedPassword = PasswordUtils.hashPassword(testPassword, salt)

                    val testUser = User(
                        username = "test_user",
                        email = testEmail,
                        password = PasswordUtils.bytesToHex(hashedPassword),
                        salt = PasswordUtils.bytesToHex(salt)
                    )

                    repository.insertUser(testUser)
                }
            } catch (e: Exception) {
                Log.e(tag, "Error adding test user", e)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    @Suppress("DEPRECATION")
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}