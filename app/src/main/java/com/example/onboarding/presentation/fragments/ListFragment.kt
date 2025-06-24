package com.example.onboarding.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onboarding.R
import com.example.onboarding.data.database.AppDatabase
import com.example.onboarding.data.repositories.PeopleRepository
import com.example.onboarding.presentation.MainActivity
import com.example.onboarding.presentation.PeopleAdapter
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    private lateinit var peopleRepository: PeopleRepository
    private lateinit var peopleAdapter: PeopleAdapter
    private lateinit var tvTotalPeople: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(view)

        tvTotalPeople = view.findViewById(R.id.tvTotalPeople)

        val db = AppDatabase.getDatabase(requireContext())
        peopleRepository = PeopleRepository(db.peopleDao())

        setupRecyclerView(view)
        loadPeople()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPeople)
        peopleAdapter = PeopleAdapter { person ->
            showPersonDetailDialog(person)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true) // ✅ Mejora de rendimiento
            adapter = peopleAdapter
        }
    }

    private fun loadPeople() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val peopleList = peopleRepository.getAllPeople()
                peopleAdapter.submitList(peopleList)
                updateTotalPeopleCount(peopleList.size)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error loading data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun showPersonDetailDialog(person: com.example.onboarding.data.entities.People) {
        val dialog = PersonDetailDialogFragment.newInstance(person)
        dialog.show(parentFragmentManager, "PersonDetailDialog")
    }

    private fun setupToolbar(view: View) {
        val toolbar = view.findViewById<View>(R.id.toolbar)
        val btnBack: ImageButton = toolbar.findViewById(R.id.btnBack)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.toolbar_title)

        toolbarTitle.text = getString(R.string.title_list)

        btnBack.setOnClickListener {
            navigateToHomeFragment()
        }
    }

    private fun navigateToHomeFragment() {
        (activity as? MainActivity)?.apply {
            navigateToFragment(HomeFragment())
            setSelectedNavigationItem(R.id.nav_home)
        }
    }
}
