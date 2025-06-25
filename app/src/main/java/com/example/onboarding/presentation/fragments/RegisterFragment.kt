package com.example.onboarding.presentation.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.onboarding.R
import com.example.onboarding.data.database.AppDatabase
import com.example.onboarding.data.entities.User
import com.example.onboarding.data.repositories.UserRepository
import com.example.onboarding.data.dto.PasswordUtils
import com.example.onboarding.presentation.activities.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var progressBar: ProgressBar

    private lateinit var repository: UserRepository

    private var backPressedListener: OnBackPressedListener? = null

    interface OnBackPressedListener {
        fun onBackPressed()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tilName = view.findViewById(R.id.tilName)
        tilEmail = view.findViewById(R.id.tilEmailRegister)
        tilPassword = view.findViewById(R.id.tilPasswordRegister)
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPasswordRegister)

        etName = view.findViewById(R.id.etName)
        etEmail = view.findViewById(R.id.etEmailRegister)
        etPassword = view.findViewById(R.id.etPasswordRegister)
        etConfirmPassword = view.findViewById(R.id.etConfirmPasswordRegister)
        btnRegister = view.findViewById(R.id.registerButton)
        progressBar = view.findViewById(R.id.progressBar)

        setupToolbar(view)

        val database = AppDatabase.getDatabase(requireContext())
        repository = UserRepository(database.userDao())

        setupListeners()
    }



    private fun setupListeners() {
        btnRegister.setOnClickListener {
            validateAndRegister()
        }
    }

    private fun performRegistration(name: String, email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (repository.emailExists(email)) {
                    requireActivity().runOnUiThread {
                        tilEmail.error = getString(R.string.error_email_exists)
                        progressBar.visibility = View.GONE
                        btnRegister.isEnabled = true
                    }
                    return@launch
                }

                val salt = PasswordUtils.generateSalt()
                val hashedPassword = PasswordUtils.hashPassword(password, salt)

                val user = User(
                    username = name,
                    email = email,
                    password = PasswordUtils.bytesToHex(hashedPassword),
                    salt = PasswordUtils.bytesToHex(salt)
                )

                val userId = repository.insertUser(user)

                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true

                    if (userId > 0) {
                        Toast.makeText(
                            requireContext(),
                            R.string.registration_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToLoginActivity()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            R.string.registration_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateAndRegister() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()


        tilName.error = null
        tilEmail.error = null
        tilPassword.error = null
        tilConfirmPassword.error = null

        var isValid = true

        if (name.isEmpty()) {
            tilName.error = getString(R.string.error_name_required)
            isValid = false
        }

        if (email.isEmpty()) {
            tilEmail.error = getString(R.string.error_email_required)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else if (password.length < 6) {
            tilPassword.error = getString(R.string.error_password_length)
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = getString(R.string.error_confirm_password_required)
            isValid = false
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = getString(R.string.error_password_mismatch)
            isValid = false
        }

        if (isValid) {
            performRegistration(name, email, password)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBackPressedListener) {
            backPressedListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        backPressedListener = null
    }

    private fun setupToolbar(view: View) {
        val toolbar = view.findViewById<View>(R.id.toolbar)
        val btnBack: ImageButton = toolbar.findViewById(R.id.btnBack)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.toolbar_title)

        toolbarTitle.text = getString(R.string.register)

        btnBack.setOnClickListener {
            navigateToLoginActivity()
        }
    }

    @Suppress("DEPRECATION")
    private fun navigateToLoginActivity() {
        val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        requireActivity().finish()
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}