package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val receiptCode: String, // e.g., FA-REC-3901
    val amount: Double,
    val paymentType: String, // "DEPOSIT", "MILESTONE", "FINAL"
    val paymentMethod: String, // "MANUAL_RECORD", "BANK_TRANSFER", "CASH", "MOBILE_MONEY"
    val notes: String = "",
    val recordedBy: String,
    val recordedAt: Long = System.currentTimeMillis()
)
