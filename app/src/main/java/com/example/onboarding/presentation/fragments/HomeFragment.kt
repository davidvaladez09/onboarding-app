package com.example.onboarding.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.onboarding.R
import com.example.onboarding.data.database.AppDatabase
import com.example.onboarding.data.entities.People
import com.example.onboarding.data.repositories.PeopleRepository
import com.example.onboarding.presentation.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

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
    private lateinit var ivProfile: ImageView

    private lateinit var peopleRepository: PeopleRepository
    private var currentPhotoPath: String? = null
    private var hasPhoto = false

    private val takePictureResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            if (bitmap != null) {
                ivProfile.setImageBitmap(bitmap)
                hasPhoto = true
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_loading_image), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val pickImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                currentPhotoPath = saveImageFromUri(uri)
                if (currentPhotoPath != null) {
                    val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                    ivProfile.setImageBitmap(bitmap)
                    hasPhoto = true
                } else {
                    Toast.makeText(requireContext(), getString(R.string.error_saving_image), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (checkCameraPermission()) {
                dispatchTakePictureIntent()
            } else if (checkStoragePermission()) {
                dispatchPickImageIntent()
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tilName = view.findViewById(R.id.tilNameHome)
        tilBirthDate = view.findViewById(R.id.tilBirthDate)
        tilAddress = view.findViewById(R.id.tilAddress)
        tilPhoneNumber = view.findViewById(R.id.tilPhoneNumber)
        tilHobbies = view.findViewById(R.id.tilHobbies)

        etName = view.findViewById(R.id.etNameHome)
        etBirthDate = view.findViewById(R.id.etBirthDate)
        etAddress = view.findViewById(R.id.etAddress)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)
        etHobbies = view.findViewById(R.id.etHobbies)
        btnRegister = view.findViewById(R.id.registerButton)
        ivProfile = view.findViewById(R.id.ivProfile)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(view)

        val database = AppDatabase.getDatabase(requireContext())
        peopleRepository = PeopleRepository(database.peopleDao())

        setupProfileImage()
        setupPhoneNumberFormatting()
        setupDatePicker()
        setupRegisterButton()
    }

    private fun setupProfileImage() {
        ivProfile.setOnClickListener {
            showImageSourceDialog()
        }
    }

    private fun setupRegisterButton() {
        btnRegister.setOnClickListener {
            registerPerson()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setupDatePicker() {
        etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                requireContext(),
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

                val digits = s.toString().replace("\\D".toRegex(), "")

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

    private fun showImageSourceDialog() {
        val options = arrayOf(
            getString(R.string.take_photo),
            getString(R.string.choose_from_gallery),
            if (hasPhoto) getString(R.string.remove_photo) else null
        ).filterNotNull()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_image_source)
            .setItems(options.toTypedArray()) { _, which ->
                when (options[which]) {
                    getString(R.string.take_photo) -> takePhoto()
                    getString(R.string.choose_from_gallery) -> pickFromGallery()
                    getString(R.string.remove_photo) -> removePhoto()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun takePhoto() {
        if (checkCameraPermission()) {
            dispatchTakePictureIntent()
        } else {
            requestCameraPermission()
        }
    }

    private fun pickFromGallery() {
        if (checkStoragePermission()) {
            dispatchPickImageIntent()
        } else {
            requestStoragePermission()
        }
    }

    private fun removePhoto() {
        currentPhotoPath = null
        hasPhoto = false
        ivProfile.setImageResource(R.drawable.ic_add_a_photo)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_msys", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val activities = requireContext().packageManager.queryIntentActivities(takePictureIntent, 0)
        val isIntentSafe = activities.isNotEmpty()

        if (!isIntentSafe) {
            showNoCameraDialog()
            return
        }

        try {
            val photoFile = createImageFile()
            currentPhotoPath = photoFile.absolutePath

            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
                takePictureResult.launch(takePictureIntent)
            } else {
                showNoCameraDialog()
            }
        } catch (ex: IOException) {
            Log.e("Camera", "Error creating image file", ex)
            Toast.makeText(requireContext(), getString(R.string.error_creating_file), Toast.LENGTH_SHORT).show()
        } catch (ex: SecurityException) {
            Log.e("Camera", "Security exception", ex)
            Toast.makeText(requireContext(), getString(R.string.permission_denied_camera), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoCameraDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.no_camera_title))
            .setMessage(getString(R.string.no_camera_message))
            .setPositiveButton(getString(R.string.choose_from_gallery)) { _, _ ->
                pickFromGallery()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    @SuppressLint("IntentReset")
    private fun dispatchPickImageIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImageResult.launch(intent)
    }

    private fun saveImageFromUri(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val timeStamp = SimpleDateFormat("yyyyMMdd_msys", Locale.getDefault()).format(Date())
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imageFile = File(storageDir, "SELECTED_$timeStamp.jpg")

            inputStream?.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output)
                }
            }
            imageFile.absolutePath
        } catch (e: IOException) {
            Log.e("HomeFragment", "Error saving image from URI", e)
            Toast.makeText(requireContext(), getString(R.string.error_saving_image), Toast.LENGTH_SHORT).show()
            null
        } catch (e: SecurityException) {
            Log.e("HomeFragment", "Security exception when saving image", e)
            Toast.makeText(requireContext(), getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
            null
        }
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
        } else if (!birthDate.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
            tilBirthDate.error = getString(R.string.error_date_format)
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
            hobbies = hobbies.ifEmpty { null },
            imagePath = currentPhotoPath
        )

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val personId = peopleRepository.insertPeople(people)

                requireActivity().runOnUiThread {
                    if (personId > 0L) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.person_registered_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        clearForm()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.registration_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    btnRegister.isEnabled = true
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_registration, e.localizedMessage),
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
        removePhoto()
    }

    private fun setupToolbar(view: View) {
        val toolbar = view.findViewById<View>(R.id.toolbar)
        val btnBack: ImageButton = toolbar.findViewById(R.id.btnBack)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.toolbar_title)

        toolbarTitle.text = getString(R.string.title_home)

        btnBack.setOnClickListener {
            navigateToLoginActivity()
        }
    }

    @Suppress("DEPRECATION")
    private fun navigateToLoginActivity() {
        val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}