package com.example.onboarding.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.onboarding.R
import com.example.onboarding.presentation.ListFragment
import com.example.onboarding.presentation.ServiceFragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnGoToList = view.findViewById<Button>(R.id.listFragment)
        val btnGoToService = view.findViewById<Button>(R.id.serviceFragment)

        btnGoToList.setOnClickListener {
            navigateToList()
        }

        btnGoToService.setOnClickListener {
            navigateToService()
        }
    }

    private fun navigateToList() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, ListFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToService() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, ServiceFragment())
            .addToBackStack(null)
            .commit()
    }
}