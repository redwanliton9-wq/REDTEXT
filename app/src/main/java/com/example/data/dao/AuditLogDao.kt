package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.AccessAuditLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM access_audit_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<AccessAuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AccessAuditLog): Long

    @Query("DELETE FROM access_audit_logs")
    suspend fun clearLogs()
}
