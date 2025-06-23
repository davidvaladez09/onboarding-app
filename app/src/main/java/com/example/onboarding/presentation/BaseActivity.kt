package com.example.onboarding.presentation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
// import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.onboarding.R
import com.example.onboarding.databinding.BottomNavigationBinding

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var navController: NavController
    private lateinit var bottomNavBinding: BottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomNavBinding = BottomNavigationBinding.inflate(layoutInflater)
    }

    protected fun setupBottomNavigation() {
        bottomNavBinding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.nav_home, R.id.nav_list, R.id.registerFragment -> {
                    showBottomNavigation()
                }
                else -> {
                    hideBottomNavigation()
                }
            }
        }
    }

    protected fun showBottomNavigation() {
        val decorView = window.decorView
        val bottomNav = decorView.findViewById<View>(R.id.bottom_navigation)
        bottomNav?.visibility = View.VISIBLE
    }

    protected fun hideBottomNavigation() {
        val decorView = window.decorView
        val bottomNav = decorView.findViewById<View>(R.id.bottom_navigation)
        bottomNav?.visibility = View.GONE
    }

    protected fun attachBottomNavigation() {
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        if (rootView is ViewGroup) {
            rootView.addView(bottomNavBinding.root)
        }
    }
}