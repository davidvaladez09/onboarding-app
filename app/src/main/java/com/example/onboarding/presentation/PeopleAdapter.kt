package com.example.onboarding.presentation

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onboarding.R
import com.example.onboarding.data.entities.People
import java.io.File

class PeopleAdapter(
    private val people: List<People>,
    private val context: Context,
    private val onItemClick: (People) -> Unit
) : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

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
        val person = people[position]
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

        holder.itemView.setOnClickListener {
            onItemClick(person)
        }
    }

    override fun getItemCount() = people.size

    private fun calculateAge(birthDate: String): String {
        return try {
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val birthDay = dateFormat.parse(birthDate)
            val today = java.util.Calendar.getInstance()
            val birthCalendar = java.util.Calendar.getInstance().apply { time = birthDay!! }

            var age = today.get(java.util.Calendar.YEAR) - birthCalendar.get(java.util.Calendar.YEAR)

            if (today.get(java.util.Calendar.DAY_OF_YEAR) < birthCalendar.get(java.util.Calendar.DAY_OF_YEAR)) {
                age--
            }

            "$age años"
        } catch (e: Exception) {
            "Edad no disponible"
        }
    }
}