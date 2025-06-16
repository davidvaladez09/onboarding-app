package com.example.onboarding.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import com.example.onboarding.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        val btnRegister = findViewById<Button>(R.id.registerButton)
        btnRegister.setOnClickListener {
            navigateToRegister()
        }

        val btnLogin = findViewById<Button>(R.id.loginButton)
        btnLogin.setOnClickListener {
            navigateToHome()
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
        // CORRECCIÓN: Navegar a MainActivity, no a HomeFragment
        val intent = Intent(this, MainActivity::class.java)

        // Opcional: Limpiar pila de actividades
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()  // Cerrar LoginActivity
    }
}