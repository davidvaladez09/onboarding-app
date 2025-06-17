package com.example.onboarding.presentation

import com.example.onboarding.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


abstract class BaseBottomNavActivity : AppCompatActivity() {
    @Suppress("DEPRECATION")
    protected fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this !is HomeActivity) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        overridePendingTransition(0, 0)
                    }
                    true
                }
                R.id.nav_list -> {
                    if (this !is ListActivity) {
                        startActivity(Intent(this, ListActivity::class.java))
                        overridePendingTransition(0, 0)
                    }
                    true
                }
                R.id.nav_service -> {
                    if (this !is ServiceActivity) {
                        startActivity(Intent(this, ServiceActivity::class.java))
                        overridePendingTransition(0, 0)
                    }
                    true
                }
                else -> false
            }
        }
    }
}