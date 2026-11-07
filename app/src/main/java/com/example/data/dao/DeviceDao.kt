package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.VerifiedDevice
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM verified_devices ORDER BY isCurrentDevice DESC, lastActiveTime DESC")
    fun getAllDevices(): Flow<List<VerifiedDevice>>

    @Query("SELECT * FROM verified_devices WHERE id = :id LIMIT 1")
    suspend fun getDeviceById(id: String): VerifiedDevice?

    @Query("SELECT * FROM verified_devices WHERE fingerprintHash = :fingerprint LIMIT 1")
    suspend fun getDeviceByFingerprint(fingerprint: String): VerifiedDevice?

    @Query("SELECT * FROM verified_devices WHERE isCurrentDevice = 1 LIMIT 1")
    suspend fun getCurrentDevice(): VerifiedDevice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: VerifiedDevice)

    @Update
    suspend fun updateDevice(device: VerifiedDevice)

    @Query("UPDATE verified_devices SET status = :status, lastActiveTime = :now WHERE id = :id")
    suspend fun updateDeviceStatus(id: String, status: String, now: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteDevice(device: VerifiedDevice)

    @Query("DELETE FROM verified_devices WHERE id = :id")
    suspend fun deleteDeviceById(id: String)
}
