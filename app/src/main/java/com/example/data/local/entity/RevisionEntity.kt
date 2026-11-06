package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revisions")
data class RevisionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val description: String,
    val referenceAttachments: String = "",
    val status: String = "PENDING", // "PENDING", "IN_PROGRESS", "COMPLETED"
    val requestedAt: Long = System.currentTimeMillis(),
    val responseNotes: String = "",
    val resolvedAt: Long? = null
)
