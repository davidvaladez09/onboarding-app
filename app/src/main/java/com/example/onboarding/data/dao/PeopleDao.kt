package com.example.onboarding.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.onboarding.data.entities.People

@Dao
interface PeopleDao {
    @Insert
    suspend fun insertPerson(person: People): Long

    @Query("SELECT * FROM people ORDER BY name ASC")
    suspend fun getAllPeople(): List<People>

    @Update
    suspend fun updatePerson(person: People)
}