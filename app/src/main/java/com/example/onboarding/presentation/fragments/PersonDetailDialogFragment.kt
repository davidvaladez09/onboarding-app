package com.example.onboarding.presentation.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.onboarding.R
import com.example.onboarding.data.entities.People
import java.io.File

class PersonDetailDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_PERSON = "person"

        fun newInstance(person: People): PersonDetailDialogFragment {
            val args = Bundle().apply {
                putSerializable(ARG_PERSON, person)
            }
            return PersonDetailDialogFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_person_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val person = arguments?.getSerializable(ARG_PERSON) as? People
        person?.let { setupViews(view, it) }

        view.findViewById<Button>(R.id.btnClose).setOnClickListener {
            dismiss()
        }
    }

    private fun setupViews(view: View, person: People) {
        view.findViewById<TextView>(R.id.tvNameDetail).text = person.name

        view.findViewById<TextView>(R.id.tvBirthDateDetail).text =
            getString(R.string.birth_date_format, person.birthDate ?: "N/A")

        val age = calculateAge(person.birthDate)
        view.findViewById<TextView>(R.id.tvAgeDetail).text =
            getString(R.string.age_format, if (age >= 0) age else 0)

        view.findViewById<TextView>(R.id.tvAddressDetail).text =
            getString(R.string.address_format, person.address ?: "N/A")

        view.findViewById<TextView>(R.id.tvPhoneDetail).text =
            getString(R.string.phone_format, formatPhoneNumber(person.phoneNumber ?: ""))

        view.findViewById<TextView>(R.id.tvHobbiesDetail).text =
            getString(R.string.hobbies_format, person.hobbies ?: getString(R.string.no_hobbies))

        /*
        val imageView = view.findViewById<ImageView>(R.id.ivProfileDetail)
        if (!person.imagePath.isNullOrEmpty()) {
            val file = File(person.imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                imageView.setImageBitmap(bitmap)
            } else {
                imageView.setImageResource(R.drawable.ic_insert_photo)
            }
        } else {
            imageView.setImageResource(R.drawable.ic_insert_photo)
        }

         */
    }

    private fun calculateAge(birthDate: String): Int {
        return try {
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val birthDay = dateFormat.parse(birthDate)
            val today = java.util.Calendar.getInstance()
            val birthCalendar = java.util.Calendar.getInstance().apply { time = birthDay!! }

            var age = today.get(java.util.Calendar.YEAR) - birthCalendar.get(java.util.Calendar.YEAR)

            if (today.get(java.util.Calendar.DAY_OF_YEAR) < birthCalendar.get(java.util.Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        } catch (e: Exception) {
            -1
        }
    }

    private fun formatPhoneNumber(phone: String): String {
        return if (phone.length == 10) {
            "${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}"
        } else {
            phone
        }
    }
}