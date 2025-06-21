package com.example.onboarding.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.onboarding.R
import com.example.onboarding.presentation.fragments.HomeFragment

class MainActivity : BaseBottomNavActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cargar fragmento inicial (Home)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        setupBottomNavigation()
        setSelectedNavigationItem(R.id.nav_home)
    }
}