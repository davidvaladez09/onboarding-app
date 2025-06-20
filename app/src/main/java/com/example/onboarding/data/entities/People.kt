package com.example.onboarding.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class People(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "birth_date")
    val birthDate: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "hobbies")
    val hobbies: String?,

    @ColumnInfo(name = "image_path")
    val imagePath: String? = null
)