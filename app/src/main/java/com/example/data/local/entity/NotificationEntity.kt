package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val projectId: Long? = null,
    val title: String,
    val message: String,
    val type: String, // "PROJECT", "QUOTATION", "MESSAGE", "REVISION", "DELIVERABLE", "PAYMENT"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
