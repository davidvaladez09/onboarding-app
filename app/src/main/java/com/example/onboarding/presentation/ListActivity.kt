package com.example.onboarding.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onboarding.R
import com.example.onboarding.data.database.AppDatabase
import com.example.onboarding.data.entities.People
import com.example.onboarding.data.repositories.PeopleRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class ListActivity : BaseBottomNavActivity() {
    private lateinit var peopleRepository: PeopleRepository
    private lateinit var tvTotalPeople: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        tvTotalPeople = findViewById(R.id.tvTotalPeople)

        val db = AppDatabase.getDatabase(applicationContext)
        peopleRepository = PeopleRepository(db.peopleDao())

        setupBottomNavigation()
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.nav_list
        setupToolbar()
        loadPeople()
    }

    private fun loadPeople() {
        lifecycleScope.launch {
            try {
                val peopleList = peopleRepository.getAllPeople()
                setupRecyclerView(peopleList)
                updateTotalPeopleCount(peopleList.size)
            } catch (e: Exception) {
                Toast.makeText(this@ListActivity, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotalPeopleCount(count: Int) {
        tvTotalPeople.text = resources.getQuantityString(
            R.plurals.total_people,
            count,
            count
        )
    }

    private fun setupRecyclerView(people: List<People>) {
        val recyclerView = findViewById<RecyclerView>(R.id.rvPeople)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PeopleAdapter(people)
    }

    private fun setupToolbar() {
        val toolbar: View = findViewById(R.id.toolbar)
        val btnBack: ImageButton = toolbar.findViewById(R.id.btnBack)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.toolbar_title)

        toolbarTitle.text = getString(R.string.title_list)

        btnBack.setOnClickListener {
            navigateToLogin()
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