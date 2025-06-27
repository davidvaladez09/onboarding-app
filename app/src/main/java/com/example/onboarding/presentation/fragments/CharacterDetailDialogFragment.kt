package com.example.onboarding.presentation.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.onboarding.R
import com.example.onboarding.data.dto.Character

class CharacterDetailDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_CHARACTER = "character"

        fun newInstance(character: Character): CharacterDetailDialogFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CHARACTER, character)
            }
            return CharacterDetailDialogFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_character_detail, container, false)
    }

    @SuppressLint("SetTextI18n")
    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val character = arguments?.getSerializable(ARG_CHARACTER) as? Character ?: return

        view.findViewById<TextView>(R.id.tvNameDetail).text = character.name
        view.findViewById<TextView>(R.id.tvStatusDetail).text = "Status: ${character.status}"
        view.findViewById<TextView>(R.id.tvSpeciesDetail).text = "Species: ${character.species}"
        view.findViewById<TextView>(R.id.tvGenderDetail).text = "Gender: ${character.gender}"
        view.findViewById<TextView>(R.id.tvOriginDetail).text = "Origin: ${character.origin.name}"
        view.findViewById<TextView>(R.id.tvLocationDetail).text = "Location: ${character.location.name}"

        view.findViewById<Button>(R.id.elevatedButton).setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setGravity(android.view.Gravity.BOTTOM)
    }
}