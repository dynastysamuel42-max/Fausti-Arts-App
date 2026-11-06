package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val description: String,
    val startingPrice: Double,
    val estimatedDays: Int,
    val sampleImageName: String = "",
    val isActive: Boolean = true
)
