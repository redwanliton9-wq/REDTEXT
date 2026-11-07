package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloud_users")
data class CloudUser(
    @PrimaryKey
    val email: String,
    val name: String,
    val password: String,
    val isOwner: Boolean = false,
    val storageTotalGb: Double = 5.0, // 5GB initial, 500GB for owner
    val storageUsedGb: Double = 0.0,
    val status: String = "ACTIVE", // ACTIVE, RESTRICTED
    val registeredAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)
