package com.example.onboarding.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.onboarding.R
import com.example.onboarding.presentation.fragments.HomeFragment

class MainActivity : BaseBottomNavActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = if (intent.getBooleanExtra("FROM_LOGIN", false)) {
                HomeFragment()
            } else {
                HomeFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        setupBottomNavigation()
        setSelectedNavigationItem(R.id.nav_home)
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}