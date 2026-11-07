package com.example.data

import com.example.data.entity.AccessAuditLog
import com.example.data.entity.CloudFile
import com.example.data.entity.CloudUser
import com.example.data.entity.RedtextItem
import com.example.data.entity.VerifiedDevice
import com.example.security.SecurityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class RedtextRepository(private val database: RedtextDatabase) {

    private val redtextDao = database.redtextDao()
    private val deviceDao = database.deviceDao()
    private val auditLogDao = database.auditLogDao()
    private val cloudUserDao = database.cloudUserDao()
    private val cloudFileDao = database.cloudFileDao()

    val allItems: Flow<List<RedtextItem>> = redtextDao.getAllItems()
    val allDevices: Flow<List<VerifiedDevice>> = deviceDao.getAllDevices()
    val recentLogs: Flow<List<AccessAuditLog>> = auditLogDao.getRecentLogs()
    val allUsers: Flow<List<CloudUser>> = cloudUserDao.getAllUsers()
    val allCloudFiles: Flow<List<CloudFile>> = cloudFileDao.getAllCloudFiles()

    fun getUserFiles(email: String): Flow<List<CloudFile>> = cloudFileDao.getFilesByUser(email)

    suspend fun getUserByEmail(email: String): CloudUser? = cloudUserDao.getUserByEmail(email)

    suspend fun insertUser(user: CloudUser) {
        cloudUserDao.insertUser(user)
    }

    suspend fun updateStorageQuota(email: String, newQuotaGb: Double) {
        cloudUserDao.updateStorageQuota(email, newQuotaGb)
    }

    suspend fun insertCloudFile(file: CloudFile) {
        cloudFileDao.insertFile(file)
        // Recalculate used storage
        recalculateStorage(file.userEmail)
    }

    suspend fun deleteCloudFile(file: CloudFile) {
        cloudFileDao.deleteFile(file)
        recalculateStorage(file.userEmail)
    }

    private suspend fun recalculateStorage(email: String) {
        val userFiles = cloudFileDao.getFilesByUser(email).first()
        val totalMb = userFiles.sumOf { it.sizeMb }
        val usedGb = String.format(java.util.Locale.US, "%.2f", totalMb / 1024.0).toDouble()
        cloudUserDao.updateStorageUsed(email, usedGb)
    }

    suspend fun insertItem(item: RedtextItem): Long {
        val encryptedContent = SecurityManager.encryptText(item.content)
        val encryptedItem = item.copy(
            content = encryptedContent,
            updatedAt = System.currentTimeMillis()
        )
        return redtextDao.insertItem(encryptedItem)
    }

    suspend fun updateItem(item: RedtextItem) {
        val encryptedContent = SecurityManager.encryptText(item.content)
        val encryptedItem = item.copy(
            content = encryptedContent,
            updatedAt = System.currentTimeMillis()
        )
        redtextDao.updateItem(encryptedItem)
    }

    suspend fun deleteItem(item: RedtextItem) {
        redtextDao.deleteItem(item)
    }

    suspend fun deleteItemById(id: Long) {
        redtextDao.deleteById(id)
    }

    suspend fun handleBurnView(item: RedtextItem): Boolean {
        if (!item.isBurnAfterReading) return false
        val remaining = item.burnViewsRemaining - 1
        if (remaining <= 0) {
            redtextDao.deleteById(item.id)
            logAction(
                action = "BURN_DESTROY",
                detail = "Self-destructed note '${item.title}' after reading",
                deviceInfo = "Current Device",
                ip = "127.0.0.1",
                success = true
            )
            return true
        } else {
            redtextDao.updateItem(item.copy(burnViewsRemaining = remaining))
            return false
        }
    }

    suspend fun insertDevice(device: VerifiedDevice) {
        deviceDao.insertDevice(device)
    }

    suspend fun updateDeviceStatus(id: String, status: String) {
        deviceDao.updateDeviceStatus(id, status)
    }

    suspend fun deleteDevice(id: String) {
        deviceDao.deleteDeviceById(id)
    }

    suspend fun logAction(action: String, detail: String, deviceInfo: String, ip: String, success: Boolean) {
        auditLogDao.insertLog(
            AccessAuditLog(
                action = action,
                detail = detail,
                deviceInfo = deviceInfo,
                ipAddress = ip,
                timestamp = System.currentTimeMillis(),
                success = success
            )
        )
    }

    suspend fun initializeDefaultDataIfNeeded(currentFingerprint: String, currentDeviceName: String) {
        // Initialize Default Cloud Users
        val existingUsers = cloudUserDao.getAllUsers().first()
        if (existingUsers.isEmpty()) {
            // Main Owner with 500GB Storage
            val owner = CloudUser(
                email = "redwanliton9@gmail.com",
                name = "Redwan (Cloud Owner)",
                password = "password123",
                isOwner = true,
                storageTotalGb = 500.0,
                storageUsedGb = 42.8,
                status = "ACTIVE"
            )
            // Regular users with 5GB initial storage
            val user1 = CloudUser(
                email = "rashed.chowdhury@gmail.com",
                name = "Rashed Chowdhury",
                password = "password123",
                isOwner = false,
                storageTotalGb = 5.0,
                storageUsedGb = 1.6,
                status = "ACTIVE"
            )
            val user2 = CloudUser(
                email = "tamim.iqbal@domain.bd",
                name = "Tamim Iqbal",
                password = "password123",
                isOwner = false,
                storageTotalGb = 5.0,
                storageUsedGb = 2.4,
                status = "ACTIVE"
            )
            val user3 = CloudUser(
                email = "nusrat.jahan@gmail.com",
                name = "Nusrat Jahan",
                password = "password123",
                isOwner = false,
                storageTotalGb = 15.0, // Upgraded by owner
                storageUsedGb = 4.2,
                status = "ACTIVE"
            )
            cloudUserDao.insertUser(owner)
            cloudUserDao.insertUser(user1)
            cloudUserDao.insertUser(user2)
            cloudUserDao.insertUser(user3)

            // Seed sample cloud files
            val sampleFiles = listOf(
                CloudFile(
                    id = UUID.randomUUID().toString(),
                    userEmail = "redwanliton9@gmail.com",
                    fileName = "Redtext_Master_Cloud_Architecture.pdf",
                    fileType = "DOC",
                    sizeMb = 14.2,
                    shareLink = "https://www.REDTEXT.com.BD/file/arch-9942",
                    contentSnippet = "Architecture spec for distributed zero-knowledge cloud storage cluster in Dhaka"
                ),
                CloudFile(
                    id = UUID.randomUUID().toString(),
                    userEmail = "redwanliton9@gmail.com",
                    fileName = "Server_Access_Keys_and_Certificates.tar.gz",
                    fileType = "ARCHIVE",
                    sizeMb = 128.5,
                    shareLink = "https://www.REDTEXT.com.BD/file/sec-cert-84",
                    contentSnippet = "TLS 1.3 root certificates, SSH RSA-4096 host keys, and cloud bastion credentials"
                ),
                CloudFile(
                    id = UUID.randomUUID().toString(),
                    userEmail = "redwanliton9@gmail.com",
                    fileName = "Encrypted_Client_Database_Backup.sql.enc",
                    fileType = "DATA",
                    sizeMb = 840.0,
                    shareLink = "https://www.REDTEXT.com.BD/file/sql-bk-102",
                    contentSnippet = "AES-256 encrypted relational database snapshot containing transaction records"
                ),
                CloudFile(
                    id = UUID.randomUUID().toString(),
                    userEmail = "rashed.chowdhury@gmail.com",
                    fileName = "Project_Financial_Report_2026.xlsx",
                    fileType = "DOC",
                    sizeMb = 8.6,
                    shareLink = "https://www.REDTEXT.com.BD/file/doc-fin-77",
                    contentSnippet = "Q3 financial balance sheet, annual revenue projections, and operating expenses"
                ),
                CloudFile(
                    id = UUID.randomUUID().toString(),
                    userEmail = "rashed.chowdhury@gmail.com",
                    fileName = "Company_Presentation_Deck.mp4",
                    fileType = "VIDEO",
                    sizeMb = 412.0,
                    shareLink = "https://www.REDTEXT.com.BD/file/vid-pres-12",
                    contentSnippet = "Executive keynote presentation and investor deck for www.REDTEXT.com.BD"
                ),
                CloudFile(
                    id = UUID.randomUUID().toString(),
                    userEmail = "tamim.iqbal@domain.bd",
                    fileName = "Product_Photos_HighRes.zip",
                    fileType = "ARCHIVE",
                    sizeMb = 1200.0,
                    shareLink = "https://www.REDTEXT.com.BD/file/img-hires-55",
                    contentSnippet = "High resolution raw marketing visual assets and photography"
                )
            )
            sampleFiles.forEach { cloudFileDao.insertFile(it) }
        }

        val existingDevices = deviceDao.getAllDevices().first()
        if (existingDevices.isEmpty()) {
            val currentDevice = VerifiedDevice(
                id = UUID.randomUUID().toString(),
                deviceName = "$currentDeviceName (This Device)",
                deviceType = "MOBILE",
                fingerprintHash = currentFingerprint,
                ipAddress = "192.168.1.42",
                status = "VERIFIED",
                isCurrentDevice = true,
                lastActiveTime = System.currentTimeMillis(),
                registeredAt = System.currentTimeMillis()
            )
            val webPortalDevice = VerifiedDevice(
                id = UUID.randomUUID().toString(),
                deviceName = "www.REDTEXT.com.BD (Web Portal Any Browser)",
                deviceType = "BROWSER",
                fingerprintHash = "SHA256:4C88...F0E9",
                ipAddress = "103.114.98.24",
                status = "VERIFIED",
                isCurrentDevice = false,
                lastActiveTime = System.currentTimeMillis() - 1000 * 60 * 5,
                registeredAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24
            )
            deviceDao.insertDevice(currentDevice)
            deviceDao.insertDevice(webPortalDevice)

            logAction(
                action = "DEVICE_VERIFY",
                detail = "Initial verified authorization established for $currentDeviceName on www.REDTEXT.com.BD",
                deviceInfo = currentDeviceName,
                ip = "192.168.1.42",
                success = true
            )
        }

        val existingItems = redtextDao.getAllItems().first()
        if (existingItems.isEmpty()) {
            val sampleItems = listOf(
                RedtextItem(
                    title = "Production Cloud Secret Token",
                    content = "rtx_sec_live_994208aef7294bcda901928374",
                    category = "Tokens",
                    tags = "cloud, prod, credentials",
                    isFavorite = true,
                    isBurnAfterReading = false
                ),
                RedtextItem(
                    title = "Remote Web Terminal SSH Key",
                    content = "-----BEGIN OPENSSH PRIVATE KEY-----\nb3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABlwAAAAdzc2gtcn\nNhAAAAAwEAAQAAAYEAv912891f7...redtext-root@www.REDTEXT.com.BD\n-----END OPENSSH PRIVATE KEY-----",
                    category = "Credentials",
                    tags = "ssh, server, admin",
                    isFavorite = false,
                    isBurnAfterReading = false
                ),
                RedtextItem(
                    title = "Encrypted Recovery Seed Phrase",
                    content = "crimson stealth cipher vault matrix obsidian radar protocol shield ruby apex",
                    category = "Secrets",
                    tags = "seed, recovery, crypto",
                    isFavorite = true,
                    isBurnAfterReading = false
                ),
                RedtextItem(
                    title = "One-Time Access Dispatch (Burner)",
                    content = "Single-use authorization code: REDTEXT-9482-SECURE-ONETIME. Self-destructs after viewing.",
                    category = "Burner",
                    tags = "burner, temp, auto-destruct",
                    isFavorite = false,
                    isBurnAfterReading = true,
                    burnViewsRemaining = 1
                )
            )

            sampleItems.forEach { insertItem(it) }
        }
    }

    suspend fun wipeAllData() {
        redtextDao.deleteAll()
        auditLogDao.clearLogs()
    }
}
