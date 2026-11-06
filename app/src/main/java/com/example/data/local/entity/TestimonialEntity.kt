package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "testimonials")
data class TestimonialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val company: String,
    val rating: Int = 5,
    val feedback: String,
    val projectTitle: String,
    val date: String
)
