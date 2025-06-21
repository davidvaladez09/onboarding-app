package com.example.onboarding.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.onboarding.R
import com.example.onboarding.presentation.BaseBottomNavActivity
import com.example.onboarding.presentation.LoginActivity

class ServiceFragment : Fragment() {

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
    }

    private fun setupToolbar(view: View) {
        val toolbar = view.findViewById<View>(R.id.toolbar)
        val btnBack: ImageButton = toolbar.findViewById(R.id.btnBack)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.toolbar_title)

        toolbarTitle.text = getString(R.string.title_service)

        btnBack.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        requireActivity().finish()
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}