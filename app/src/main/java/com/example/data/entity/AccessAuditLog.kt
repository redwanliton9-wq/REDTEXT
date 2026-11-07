package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "access_audit_logs")
data class AccessAuditLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String, // EMAIL_VERIFY, DEVICE_VERIFY, VAULT_READ, VAULT_WRITE, BURN_DESTROY, REVOKE_DEVICE
    val detail: String,
    val deviceInfo: String,
    val ipAddress: String,
    val timestamp: Long = System.currentTimeMillis(),
    val success: Boolean = true
)
