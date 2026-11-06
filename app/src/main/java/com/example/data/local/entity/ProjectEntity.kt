package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectCode: String, // e.g., FA-8492
    val clientId: Long,
    val clientName: String,
    val managerId: Long? = null,
    val managerName: String? = null,
    val title: String,
    val serviceCategory: String,
    val description: String,
    val referenceAttachments: String = "", // comma-separated or summary
    val deadline: String,
    val budget: Double,
    val agreedPrice: Double? = null,
    val specialInstructions: String = "",
    val status: String, // 1. Request Received, 2. Under Review, 3. Price Negotiation, 4. Awaiting Approval, 5. Approved, 6. In Production, 7. Quality Review, 8. Revision Requested, 9. Completed, 10. Delivered
    val currentQuotation: Double? = null,
    val paidAmount: Double = 0.0,
    val paymentStatus: String = "UNPAID", // "UNPAID", "PARTIALLY_PAID", "PAID"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
