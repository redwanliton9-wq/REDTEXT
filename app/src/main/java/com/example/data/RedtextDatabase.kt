package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AuditLogDao
import com.example.data.dao.CloudFileDao
import com.example.data.dao.CloudUserDao
import com.example.data.dao.DeviceDao
import com.example.data.dao.RedtextDao
import com.example.data.entity.AccessAuditLog
import com.example.data.entity.CloudFile
import com.example.data.entity.CloudUser
import com.example.data.entity.RedtextItem
import com.example.data.entity.VerifiedDevice

@Database(
    entities = [
        RedtextItem::class,
        VerifiedDevice::class,
        AccessAuditLog::class,
        CloudUser::class,
        CloudFile::class
    ],
    version = 3,
    exportSchema = false
)
abstract class RedtextDatabase : RoomDatabase() {
    abstract fun redtextDao(): RedtextDao
    abstract fun deviceDao(): DeviceDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun cloudUserDao(): CloudUserDao
    abstract fun cloudFileDao(): CloudFileDao

    companion object {
        @Volatile
        private var INSTANCE: RedtextDatabase? = null

        fun getDatabase(context: Context): RedtextDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RedtextDatabase::class.java,
                    "redtext_vault.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
