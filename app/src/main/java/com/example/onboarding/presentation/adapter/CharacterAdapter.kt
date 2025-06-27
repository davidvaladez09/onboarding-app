package com.example.onboarding.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onboarding.data.dto.Character
import com.example.onboarding.databinding.ItemCharacterBinding

class CharacterAdapter(
    private val onItemClick: (Character) -> Unit
) : ListAdapter<Character, CharacterAdapter.CharacterViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)
        holder.bind(character)
    }

    inner class CharacterViewHolder(
        private val binding: ItemCharacterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character) {
            with(binding) {
                tvName.text = character.name
                tvSpecies.text = character.species
                tvStatus.text = character.status

                Glide.with(root.context)
                    .load(character.image)
                    .into(ivCharacter)

                root.setOnClickListener { onItemClick(character) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem == newItem
        }
    }
}