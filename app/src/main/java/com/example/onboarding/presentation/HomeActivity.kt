package com.example.onboarding.presentation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.onboarding.R
import com.example.onboarding.data.database.AppDatabase
import com.example.onboarding.data.entities.People
import com.example.onboarding.data.repositories.PeopleRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeActivity : BaseBottomNavActivity() {

    private lateinit var tilName: TextInputLayout
    private lateinit var tilBirthDate: TextInputLayout
    private lateinit var tilAddress: TextInputLayout
    private lateinit var tilPhoneNumber: TextInputLayout
    private lateinit var tilHobbies: TextInputLayout

    private lateinit var etName: TextInputEditText
    private lateinit var etBirthDate: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etPhoneNumber: TextInputEditText
    private lateinit var etHobbies: TextInputEditText
    private lateinit var btnRegister: MaterialButton

    private lateinit var peopleRepository: PeopleRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContentView(R.layout.activity_home)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val database = AppDatabase.getDatabase(this)
        peopleRepository = PeopleRepository(database.peopleDao())

        setupBottomNavigation()
        setupToolbar()
        setupViews()
        setupPhoneNumberFormatting()
        setupDatePicker()
        setupRegisterButton()

        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.nav_home
    }

    override fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_list -> {
                    startActivity(Intent(this, ListActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupToolbar() {
        val toolbar: View = findViewById(R.id.toolbar)
        val btnBack: ImageButton = toolbar.findViewById(R.id.btnBack)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.toolbar_title)

        toolbarTitle.text = getString(R.string.title_home)

        btnBack.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun setupViews() {
        tilName = findViewById(R.id.tilNameHome)
        tilBirthDate = findViewById(R.id.tilBirthDate)
        tilAddress = findViewById(R.id.tilAddress)
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber)
        tilHobbies = findViewById(R.id.tilHobbies)

        etName = findViewById(R.id.etNameHome)
        etBirthDate = findViewById(R.id.etBirthDate)
        etAddress = findViewById(R.id.etAddress)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etHobbies = findViewById(R.id.etHobbies)
        btnRegister = findViewById(R.id.registerButton)
    }

    private fun setupRegisterButton() {
        btnRegister.setOnClickListener {
            registerPerson()
        }
    }

    private fun setupDatePicker() {
        etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                    etBirthDate.setText(formattedDate)
                },
                year,
                month,
                day
            ).show()
        }
    }

    private fun setupPhoneNumberFormatting() {
        etPhoneNumber.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true

                val digits = s.toString().replace("[^\\d]".toRegex(), "")

                val formatted = when {
                    digits.length <= 3 -> digits
                    digits.length <= 7 -> "${digits.substring(0, 3)}-${digits.substring(3)}"
                    else -> "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7)}"
                }

                s?.replace(0, s.length, formatted)

                isFormatting = false
            }
        })
    }

    private fun registerPerson() {
        val name = etName.text.toString().trim()
        val birthDate = etBirthDate.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().replace("-", "")
        val hobbies = etHobbies.text.toString().trim()

        var isValid = true
        tilName.error = null
        tilBirthDate.error = null
        tilAddress.error = null
        tilPhoneNumber.error = null

        if (name.isEmpty()) {
            tilName.error = getString(R.string.error_name_required)
            isValid = false
        }

        if (birthDate.isEmpty()) {
            tilBirthDate.error = getString(R.string.error_birthdate_required)
            isValid = false
        }

        if (address.isEmpty()) {
            tilAddress.error = getString(R.string.error_address_required)
            isValid = false
        }

        if (phoneNumber.isEmpty()) {
            tilPhoneNumber.error = getString(R.string.error_phone_required)
            isValid = false
        } else if (phoneNumber.length < 10) {
            tilPhoneNumber.error = getString(R.string.error_phone_invalid)
            isValid = false
        }

        if (!isValid) return

        btnRegister.isEnabled = false

        val people = People(
            name = name,
            birthDate = birthDate,
            address = address,
            phoneNumber = phoneNumber,
            hobbies = if (hobbies.isEmpty()) null else hobbies
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val personId = peopleRepository.insertPeople(people)

                runOnUiThread {
                    if (personId > 0L) {
                        Toast.makeText(
                            this@HomeActivity,
                            getString(R.string.person_registered_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        clearForm()
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            getString(R.string.registration_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    btnRegister.isEnabled = true
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.error_registration, e.message),
                        Toast.LENGTH_SHORT
                    ).show()
                    btnRegister.isEnabled = true
                }
            }
        }
    }

    private fun clearForm() {
        etName.text?.clear()
        etBirthDate.text?.clear()
        etAddress.text?.clear()
        etPhoneNumber.text?.clear()
        etHobbies.text?.clear()
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