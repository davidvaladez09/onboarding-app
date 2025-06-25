package com.example.onboarding.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onboarding.R
import com.example.onboarding.data.database.AppDatabase
import com.example.onboarding.data.entities.User
import com.example.onboarding.data.repositories.UserRepository
import com.example.onboarding.data.dto.PasswordUtils
import com.example.onboarding.presentation.MainActivity
import com.example.onboarding.presentation.fragments.RegisterFragment
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
            showRegisterFragment()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
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
                                navigateToMainActivity()
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
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

    private fun showRegisterFragment() {
        findViewById<ScrollView>(R.id.login_scroll_view).visibility = View.GONE

        findViewById<FrameLayout>(R.id.register_fragment_container).visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.register_fragment_container, RegisterFragment())
            .commit()
    }

    @Suppress("DEPRECATION")
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("FROM_LOGIN", true)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}