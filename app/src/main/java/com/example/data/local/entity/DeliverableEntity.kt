package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deliverables")
data class DeliverableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val title: String,
    val fileType: String, // "IMAGE", "PDF", "VECTOR", "ARCHIVE", "VIDEO"
    val fileName: String,
    val fileSize: String,
    val downloadUrl: String,
    val notes: String = "",
    val uploadedBy: String,
    val uploadedAt: Long = System.currentTimeMillis(),
    val isApproved: Boolean = false
)
