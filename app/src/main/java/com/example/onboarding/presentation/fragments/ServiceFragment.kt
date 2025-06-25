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
import com.example.onboarding.data.dto.Character
import com.example.onboarding.data.repositories.CharacterRepository
import com.example.onboarding.presentation.activities.BaseBottomNavActivity
import com.example.onboarding.presentation.adapter.CharacterAdapter
import com.example.onboarding.presentation.MainActivity
import kotlinx.coroutines.launch

class ServiceFragment : Fragment() {

    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var tvTotalCharacters: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateView: LinearLayout
    private val repository = CharacterRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? BaseBottomNavActivity)?.setSelectedNavigationItem(R.id.nav_service)
        setupToolbar(view)

        tvTotalCharacters = view.findViewById(R.id.tvTotalCharacters)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.rvCharacters)
        emptyStateView = view.findViewById(R.id.emptyStateView)

        setupRecyclerView()
        loadCharacters()
    }

    private fun setupRecyclerView() {
        characterAdapter = CharacterAdapter { character ->
            showCharacterDetail(character)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = characterAdapter
        }
    }

    private fun loadCharacters() {
        toggleLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val characters = repository.getCharacters()

                if (!isAdded || view == null) return@launch

                characterAdapter.submitList(characters)
                updateTotalCharactersCount(characters.size)
                hideEmptyState()

            } catch (e: Exception) {
                if (!isAdded) return@launch

                showEmptyState()
                showErrorToast("Error: ${e.message ?: "Unknown error"}")

                e.printStackTrace()
            } finally {
                if (isAdded) {
                    toggleLoading(false)
                }
            }
        }
    }

    private fun showErrorToast(message: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showCharacterDetail(character: Character) {
        Toast.makeText(requireContext(), "Selected: ${character.name}", Toast.LENGTH_SHORT).show()
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

    private fun updateTotalCharactersCount(count: Int) {
        tvTotalCharacters.text = resources.getQuantityString(
            R.plurals.total_characters,
            count,
            count
        )
    }

    private fun setupToolbar(view: View) {
        val toolbar = view.findViewById<View>(R.id.toolbar)
        val btnBack: ImageButton = toolbar.findViewById(R.id.btnBack)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.toolbar_title)

        toolbarTitle.text = getString(R.string.title_service)

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