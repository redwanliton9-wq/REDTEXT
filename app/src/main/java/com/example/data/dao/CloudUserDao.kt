package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CloudUser
import kotlinx.coroutines.flow.Flow

@Dao
interface CloudUserDao {
    @Query("SELECT * FROM cloud_users ORDER BY isOwner DESC, registeredAt ASC")
    fun getAllUsers(): Flow<List<CloudUser>>

    @Query("SELECT * FROM cloud_users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): CloudUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: CloudUser)

    @Update
    suspend fun updateUser(user: CloudUser)

    @Query("UPDATE cloud_users SET storageTotalGb = :newQuota WHERE email = :email")
    suspend fun updateStorageQuota(email: String, newQuota: Double)

    @Query("UPDATE cloud_users SET storageUsedGb = :used WHERE email = :email")
    suspend fun updateStorageUsed(email: String, used: Double)

    @Delete
    suspend fun deleteUser(user: CloudUser)
}
