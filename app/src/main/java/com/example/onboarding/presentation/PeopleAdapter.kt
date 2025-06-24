package com.example.onboarding.presentation

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.onboarding.R
import com.example.onboarding.data.entities.People
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PeopleAdapter(
    private val onItemClick: (People) -> Unit
) : ListAdapter<People, PeopleAdapter.PeopleViewHolder>(PeopleDiffCallback()) {

    inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAge: TextView = itemView.findViewById(R.id.tvAge)
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return PeopleViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val person = getItem(position)
        holder.tvName.text = person.name
        holder.tvAge.text = calculateAge(person.birthDate)

        if (!person.imagePath.isNullOrEmpty()) {
            val file = File(person.imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.ivProfileImage.setImageBitmap(bitmap)
            } else {
                holder.ivProfileImage.setImageResource(R.drawable.ic_insert_photo)
            }
        } else {
            holder.ivProfileImage.setImageResource(R.drawable.ic_insert_photo)
        }

        holder.itemView.setOnClickListener { onItemClick(person) }
    }

    private fun calculateAge(birthDate: String): String {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val birthDay = dateFormat.parse(birthDate)
            val today = Calendar.getInstance()
            val birthCalendar = Calendar.getInstance().apply { time = birthDay!! }

            var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) age--
            "$age years"
        } catch (e: Exception) {
            "Age not available"
        }
    }
}

class PeopleDiffCallback : DiffUtil.ItemCallback<People>() {
    override fun areItemsTheSame(oldItem: People, newItem: People): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: People, newItem: People): Boolean = oldItem == newItem
}
