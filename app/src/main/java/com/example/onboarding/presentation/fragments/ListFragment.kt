package com.example.onboarding.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onboarding.R
import com.example.onboarding.data.database.AppDatabase
import com.example.onboarding.data.entities.People
import com.example.onboarding.data.repositories.PeopleRepository
import com.example.onboarding.presentation.PeopleAdapter
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    private lateinit var peopleRepository: PeopleRepository
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

        tvTotalPeople = view.findViewById(R.id.tvTotalPeople)

        val db = AppDatabase.getDatabase(requireContext())
        peopleRepository = PeopleRepository(db.peopleDao())

        loadPeople()
    }

    private fun loadPeople() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val peopleList = peopleRepository.getAllPeople()
                setupRecyclerView(peopleList)
                updateTotalPeopleCount(peopleList.size)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error al cargar datos: ${e.message}",
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

    private fun setupRecyclerView(people: List<People>) {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.rvPeople)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = PeopleAdapter(people, requireContext())
    }
}