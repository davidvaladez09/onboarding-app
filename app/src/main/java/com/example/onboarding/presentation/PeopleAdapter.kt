package com.example.onboarding.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onboarding.R
import com.example.onboarding.data.entities.People
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class PeopleAdapter(private val people: List<People>) : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAge: TextView = itemView.findViewById(R.id.tvAge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return PeopleViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val person = people[position]
        holder.tvName.text = person.name
        holder.tvAge.text = calculateAge(person.birthDate)
    }

    override fun getItemCount() = people.size

    private fun calculateAge(birthDate: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDateObj = LocalDate.parse(birthDate, formatter)
            val today = LocalDate.now()
            val period = Period.between(birthDateObj, today)
            "${period.years} years"
        } catch (e: Exception) {
            "Age not available"
        }
    }
}