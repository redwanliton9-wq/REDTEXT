package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloud_files")
data class CloudFile(
    @PrimaryKey
    val id: String,
    val userEmail: String,
    val fileName: String,
    val fileType: String, // DOC, IMAGE, VIDEO, ARCHIVE, CODE, DATA
    val sizeMb: Double,
    val shareLink: String,
    val contentSnippet: String = "",
    val isEncrypted: Boolean = true,
    val uploadedAt: Long = System.currentTimeMillis()
)
