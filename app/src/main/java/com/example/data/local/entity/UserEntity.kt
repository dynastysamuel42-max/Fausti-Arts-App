package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: String, // "CLIENT", "MANAGER", "ADMIN"
    val phone: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val permissionsJson: String = "[]", // for managers: ["MANAGE_PROJECTS", "COMMUNICATE", "UPLOAD_DELIVERABLES"]
    val createdAt: Long = System.currentTimeMillis()
)
