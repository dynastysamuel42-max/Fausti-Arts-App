package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "negotiations")
data class QuotationNegotiationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val senderRole: String, // "CLIENT", "ADMIN", "MANAGER"
    val senderName: String,
    val proposedPrice: Double,
    val note: String,
    val status: String, // "PENDING", "ACCEPTED", "REJECTED", "COUNTERED"
    val timestamp: Long = System.currentTimeMillis()
)
