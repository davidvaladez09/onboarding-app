package com.example.onboarding.data.repositories

import com.example.onboarding.data.dao.PeopleDao
import com.example.onboarding.data.entities.People
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PeopleRepository(private val peopleDao: PeopleDao) {
    suspend fun insertPeople(person: People) = withContext(Dispatchers.IO) {
        peopleDao.insertPerson(person)
    }

    suspend fun getAllPeople() = withContext(Dispatchers.IO) {
        peopleDao.getAllPeople()
    }

    suspend fun updatePeople(people: People) = withContext(Dispatchers.IO) {
        peopleDao.updatePerson(people)
    }
}