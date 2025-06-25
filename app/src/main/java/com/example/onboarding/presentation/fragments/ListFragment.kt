package com.example.onboarding.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
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
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateView: LinearLayout

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
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.rvPeople)

        val db = AppDatabase.getDatabase(requireContext())
        peopleRepository = PeopleRepository(db.peopleDao())

        setupRecyclerView()
        loadPeople()
        emptyStateView = view.findViewById(R.id.emptyStateView)
    }

    private fun setupRecyclerView() {
        peopleAdapter = PeopleAdapter { person ->
            showPersonDetailDialog(person)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = peopleAdapter
        }
    }

    private fun loadPeople() {
        toggleLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val peopleList = peopleRepository.getAllPeople()
                peopleAdapter.submitList(peopleList)
                updateTotalPeopleCount(peopleList.size)

                if (peopleList.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                }

                toggleLoading(false)
            } catch (e: Exception) {
                toggleLoading(false)
                Toast.makeText(
                    requireContext(),
                    "Error loading data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showEmptyState() {
        emptyStateView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun hideEmptyState() {
        emptyStateView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun toggleLoading(show: Boolean) {
        if (show) {
            recyclerView.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    recyclerView.visibility = View.GONE
                }
                .start()

            progressBar.visibility = View.VISIBLE
            progressBar.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        } else {
            progressBar.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    progressBar.visibility = View.GONE
                }
                .start()

            recyclerView.visibility = View.VISIBLE
            recyclerView.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
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
