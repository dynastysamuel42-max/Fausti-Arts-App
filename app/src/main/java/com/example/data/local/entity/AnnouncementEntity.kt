package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val date: String,
    val author: String = "Fausti Arts Studio",
    val priority: String = "NORMAL" // "NORMAL", "HIGH", "URGENT"
)
