package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.CloudFile
import kotlinx.coroutines.flow.Flow

@Dao
interface CloudFileDao {
    @Query("SELECT * FROM cloud_files WHERE userEmail = :userEmail ORDER BY uploadedAt DESC")
    fun getFilesByUser(userEmail: String): Flow<List<CloudFile>>

    @Query("SELECT * FROM cloud_files ORDER BY uploadedAt DESC")
    fun getAllCloudFiles(): Flow<List<CloudFile>>

    @Query("SELECT * FROM cloud_files WHERE id = :id LIMIT 1")
    suspend fun getFileById(id: String): CloudFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: CloudFile)

    @Delete
    suspend fun deleteFile(file: CloudFile)

    @Query("DELETE FROM cloud_files WHERE id = :id")
    suspend fun deleteFileById(id: String)
}
