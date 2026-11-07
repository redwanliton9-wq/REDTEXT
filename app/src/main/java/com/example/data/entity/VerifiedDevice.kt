package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verified_devices")
data class VerifiedDevice(
    @PrimaryKey
    val id: String,
    val deviceName: String,
    val deviceType: String, // MOBILE, BROWSER, DESKTOP
    val fingerprintHash: String,
    val ipAddress: String,
    val status: String, // VERIFIED, PENDING_AUTH, REVOKED
    val isCurrentDevice: Boolean = false,
    val lastActiveTime: Long = System.currentTimeMillis(),
    val registeredAt: Long = System.currentTimeMillis()
)
