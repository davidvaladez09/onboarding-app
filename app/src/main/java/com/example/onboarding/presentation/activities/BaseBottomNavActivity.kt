package com.example.onboarding.presentation.activities

import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.onboarding.R
import com.example.onboarding.presentation.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseBottomNavActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    protected open fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> navigateToFragment(HomeFragment())
                R.id.nav_list -> navigateToFragment(ListFragment())
                R.id.nav_service -> navigateToFragment(ServiceFragment())
                else -> false
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment): Boolean {
        if (supportFragmentManager.findFragmentById(R.id.fragment_container)?.javaClass != fragment.javaClass) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }

    fun setSelectedNavigationItem(itemId: Int) {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = itemId
    }

    protected fun setupToolbarBackButton(backAction: () -> Unit = { onBackPressedDispatcher.onBackPressed() }) {
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack?.setOnClickListener {
            backAction()
        }
    }
}