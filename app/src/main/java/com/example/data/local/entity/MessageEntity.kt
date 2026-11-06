package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val senderId: Long,
    val senderName: String,
    val senderRole: String, // "CLIENT", "ADMIN", "MANAGER"
    val content: String,
    val attachmentType: String = "NONE", // "NONE", "IMAGE", "DOCUMENT"
    val attachmentName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
