package com.example.onboarding.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onboarding.R
import com.example.onboarding.data.database.AppDatabase
import com.example.onboarding.data.repositories.UserRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton

    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContentView(R.layout.activity_register)

        setupToolbar()
        initViews()
        setupListeners()

        val database = AppDatabase.getDatabase(this)
        repository = UserRepository(database.userDao())
    }

    private fun performRegistration(name: String, email: String, password: String) {
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (repository.emailExists(email)) {
                    runOnUiThread {
                        tilEmail.error = getString(R.string.error_email_exists)
                        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    }
                    return@launch
                }

                val userId = repository.registerUser(name, email, password)

                runOnUiThread {
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    if (userId > 0) {
                        Toast.makeText(
                            this@RegisterActivity,
                            R.string.registration_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToLogin()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            R.string.registration_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupToolbar() {
        val toolbar: View = findViewById(R.id.toolbar)
        val btnBack: ImageButton = toolbar.findViewById(R.id.btnBack)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.toolbar_title)

        toolbarTitle.text = getString(R.string.register)

        btnBack.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun initViews() {
        tilName = findViewById(R.id.tilName)
        tilEmail = findViewById(R.id.tilEmailRegister)
        tilPassword = findViewById(R.id.tilPasswordRegister)
        tilConfirmPassword = findViewById(R.id.tilConfirmPasswordRegister)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmailRegister)
        etPassword = findViewById(R.id.etPasswordRegister)
        etConfirmPassword = findViewById(R.id.etConfirmPasswordRegister)
        btnRegister = findViewById(R.id.registerButton)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            validateAndRegister()
        }
    }

    private fun validateAndRegister() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        tilName.error = null
        tilEmail.error = null
        tilPassword.error = null
        tilConfirmPassword.error = null

        var isValid = true

        if (name.isEmpty()) {
            tilName.error = getString(R.string.error_name_required)
            isValid = false
        }

        if (email.isEmpty()) {
            tilEmail.error = getString(R.string.error_email_required)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else if (password.length < 6) {
            tilPassword.error = getString(R.string.error_password_length)
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = getString(R.string.error_confirm_password_required)
            isValid = false
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = getString(R.string.error_password_mismatch)
            isValid = false
        }

        if (isValid) {
            performRegistration(name, email, password)
        }
    }

    @Suppress("DEPRECATION")
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}