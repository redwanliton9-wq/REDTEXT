package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "redtext_items")
data class RedtextItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Notes", // Secrets, Credentials, Tokens, Notes, Burner
    val tags: String = "",
    val isFavorite: Boolean = false,
    val isBurnAfterReading: Boolean = false,
    val burnViewsRemaining: Int = 1,
    val isEncrypted: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
