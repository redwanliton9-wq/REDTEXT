package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RedtextDatabase
import com.example.data.RedtextRepository
import com.example.data.entity.AccessAuditLog
import com.example.data.entity.CloudFile
import com.example.data.entity.CloudUser
import com.example.data.entity.RedtextItem
import com.example.data.entity.VerifiedDevice
import com.example.security.SecurityManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AuthStage {
    EMAIL_INPUT,
    EMAIL_CODE_VERIFY,
    DEVICE_CHALLENGE,
    PIN_LOCK,
    AUTHENTICATED
}

enum class VaultTab {
    CLOUD_FILES,    // Cloud Access Any Data of People
    OWNER_PANEL,    // Owner controls 500GB & updates user storage day to day
    WEB_BROWSER,    // www.REDTEXT.com.BD Browser Portal
    DEVICES,        // Verified Any Man or Device
    ENCRYPTED_TEXTS // Redtext Encrypted Notes & Tokens
}

data class AuthUiState(
    val stage: AuthStage = AuthStage.EMAIL_INPUT,
    val email: String = "redwanliton9@gmail.com",
    val passwordInput: String = "password123",
    val isRegistering: Boolean = false,
    val registerName: String = "",
    val generatedCode: String = "",
    val codeInput: String = "",
    val errorMessage: String? = null,
    val successNotice: String? = null,
    val isCodeSending: Boolean = false,
    val deviceFingerprint: String = "",
    val deviceName: String = "",
    val isDeviceAuthorized: Boolean = false,
    val userPin: String = "1234",
    val pinInput: String = "",
    val currentUser: CloudUser? = null,
    val websiteUrl: String = "https://www.REDTEXT.com.BD",
    val securityScore: Int = 99
)

class RedtextViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RedtextRepository
    private val context = application.applicationContext

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(VaultTab.CLOUD_FILES)
    val selectedTab: StateFlow<VaultTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _revealedItemIds = MutableStateFlow<Set<Long>>(emptySet())
    val revealedItemIds: StateFlow<Set<Long>> = _revealedItemIds.asStateFlow()

    val allItems: StateFlow<List<RedtextItem>>
    val allDevices: StateFlow<List<VerifiedDevice>>
    val recentLogs: StateFlow<List<AccessAuditLog>>
    val allUsers: StateFlow<List<CloudUser>>
    val allCloudFiles: StateFlow<List<CloudFile>>

    init {
        val database = RedtextDatabase.getDatabase(context)
        repository = RedtextRepository(database)

        allItems = repository.allItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allDevices = repository.allDevices.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        recentLogs = repository.recentLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allUsers = repository.allUsers.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allCloudFiles = repository.allCloudFiles.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        val fingerprint = SecurityManager.getDeviceFingerprint(context)
        val devName = SecurityManager.getDeviceName()

        _authUiState.value = _authUiState.value.copy(
            deviceFingerprint = fingerprint,
            deviceName = devName
        )

        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded(fingerprint, devName)
            val ownerUser = repository.getUserByEmail("redwanliton9@gmail.com")
            _authUiState.value = _authUiState.value.copy(currentUser = ownerUser)
        }
    }

    fun onEmailChanged(newEmail: String) {
        _authUiState.value = _authUiState.value.copy(
            email = newEmail.trim(),
            errorMessage = null
        )
    }

    fun onPasswordChanged(password: String) {
        _authUiState.value = _authUiState.value.copy(
            passwordInput = password,
            errorMessage = null
        )
    }

    fun onRegisterNameChanged(name: String) {
        _authUiState.value = _authUiState.value.copy(
            registerName = name,
            errorMessage = null
        )
    }

    fun toggleRegisterMode() {
        val current = _authUiState.value.isRegistering
        _authUiState.value = _authUiState.value.copy(
            isRegistering = !current,
            errorMessage = null
        )
    }

    fun submitEmailForVerification() {
        val email = _authUiState.value.email
        val password = _authUiState.value.passwordInput

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authUiState.value = _authUiState.value.copy(
                errorMessage = "Please enter a valid email address (e.g. name@domain.com)"
            )
            return
        }

        if (password.length < 4) {
            _authUiState.value = _authUiState.value.copy(
                errorMessage = "Password must be at least 4 characters"
            )
            return
        }

        val code = SecurityManager.generateVerificationCode()
        _authUiState.value = _authUiState.value.copy(
            generatedCode = code,
            stage = AuthStage.EMAIL_CODE_VERIFY,
            codeInput = "",
            errorMessage = null,
            successNotice = "OTP security code sent to $email"
        )

        viewModelScope.launch {
            repository.logAction(
                action = "EMAIL_OTP_SENT",
                detail = "Dispatched OTP authorization code to $email for www.REDTEXT.com.BD",
                deviceInfo = _authUiState.value.deviceName,
                ip = "103.114.98.24",
                success = true
            )
        }
    }

    fun onCodeInputChanged(code: String) {
        if (code.length <= 6) {
            _authUiState.value = _authUiState.value.copy(
                codeInput = code,
                errorMessage = null
            )
            if (code.length == 6) {
                verifyCode(code)
            }
        }
    }

    fun fillSampleCode() {
        val code = _authUiState.value.generatedCode
        if (code.isNotEmpty()) {
            onCodeInputChanged(code)
        }
    }

    fun verifyCode(code: String) {
        if (code == _authUiState.value.generatedCode) {
            val email = _authUiState.value.email
            viewModelScope.launch {
                var user = repository.getUserByEmail(email)
                if (user == null) {
                    val isOwner = email.equals("redwanliton9@gmail.com", ignoreCase = true)
                    user = CloudUser(
                        email = email,
                        name = if (_authUiState.value.registerName.isNotBlank()) _authUiState.value.registerName else email.substringBefore("@"),
                        password = _authUiState.value.passwordInput,
                        isOwner = isOwner,
                        storageTotalGb = if (isOwner) 500.0 else 5.0,
                        storageUsedGb = 0.0,
                        status = "ACTIVE"
                    )
                    repository.insertUser(user)
                }

                _authUiState.value = _authUiState.value.copy(
                    currentUser = user,
                    stage = AuthStage.DEVICE_CHALLENGE,
                    errorMessage = null,
                    successNotice = "OTP verified for $email. Authorize device for cloud access."
                )

                repository.logAction(
                    action = "OTP_VERIFIED",
                    detail = "Email $email verified with OTP on www.REDTEXT.com.BD",
                    deviceInfo = _authUiState.value.deviceName,
                    ip = "103.114.98.24",
                    success = true
                )
            }
        } else {
            _authUiState.value = _authUiState.value.copy(
                errorMessage = "Invalid OTP code. Please enter the 6-digit code shown."
            )
        }
    }

    fun resendCode() {
        val newCode = SecurityManager.generateVerificationCode()
        _authUiState.value = _authUiState.value.copy(
            generatedCode = newCode,
            codeInput = "",
            errorMessage = null,
            successNotice = "New 6-digit OTP code dispatched"
        )
    }

    fun authorizeCurrentDevice() {
        _authUiState.value = _authUiState.value.copy(
            isDeviceAuthorized = true,
            stage = AuthStage.AUTHENTICATED,
            errorMessage = null
        )

        viewModelScope.launch {
            repository.logAction(
                action = "DEVICE_AUTHORIZED",
                detail = "Hardware authorized for ${_authUiState.value.email} on www.REDTEXT.com.BD",
                deviceInfo = _authUiState.value.deviceName,
                ip = "103.114.98.24",
                success = true
            )
        }
    }

    fun lockVault() {
        _authUiState.value = _authUiState.value.copy(
            stage = AuthStage.PIN_LOCK,
            pinInput = "",
            errorMessage = null
        )
    }

    fun onPinInputChanged(pin: String) {
        if (pin.length <= 4) {
            _authUiState.value = _authUiState.value.copy(pinInput = pin, errorMessage = null)
            if (pin.length == 4) {
                if (pin == _authUiState.value.userPin) {
                    _authUiState.value = _authUiState.value.copy(
                        stage = AuthStage.AUTHENTICATED,
                        pinInput = "",
                        errorMessage = null
                    )
                } else {
                    _authUiState.value = _authUiState.value.copy(
                        errorMessage = "Incorrect PIN. Default is 1234."
                    )
                }
            }
        }
    }

    fun unlockWithBiometrics() {
        _authUiState.value = _authUiState.value.copy(
            stage = AuthStage.AUTHENTICATED,
            pinInput = "",
            errorMessage = null
        )
    }

    fun loginWithBiometrics(targetEmail: String? = null) {
        val email = targetEmail ?: _authUiState.value.currentUser?.email ?: _authUiState.value.email.ifBlank { "redwanliton9@gmail.com" }
        viewModelScope.launch {
            var user = repository.getUserByEmail(email)
            if (user == null) {
                val isOwner = email.equals("redwanliton9@gmail.com", ignoreCase = true)
                user = CloudUser(
                    email = email,
                    name = if (isOwner) "Redwan Liton (Master Owner)" else email.substringBefore("@"),
                    password = "password123",
                    isOwner = isOwner,
                    storageTotalGb = if (isOwner) 500.0 else 5.0,
                    storageUsedGb = 0.0,
                    status = "ACTIVE"
                )
                repository.insertUser(user)
            }

            _authUiState.value = _authUiState.value.copy(
                currentUser = user,
                email = email,
                isDeviceAuthorized = true,
                stage = AuthStage.AUTHENTICATED,
                errorMessage = null,
                pinInput = "",
                successNotice = "Biometric authenticated for $email. Quick vault access granted."
            )

            repository.logAction(
                action = "BIOMETRIC_AUTH_SUCCESS",
                detail = "Biometric (Fingerprint/Face) unlocked vault for $email on www.REDTEXT.com.BD",
                deviceInfo = _authUiState.value.deviceName,
                ip = "103.114.98.24",
                success = true
            )
        }
    }

    fun switchAccount(targetEmail: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(targetEmail)
            if (user != null) {
                _authUiState.value = _authUiState.value.copy(
                    currentUser = user,
                    email = user.email
                )
            }
        }
    }

    fun logout() {
        _authUiState.value = _authUiState.value.copy(
            stage = AuthStage.EMAIL_INPUT,
            codeInput = ""
        )
    }

    fun selectTab(tab: VaultTab) {
        _selectedTab.value = tab
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    // Owner controls: Update day-to-day cloud storage for users
    fun updateUserStorageQuota(targetEmail: String, newQuotaGb: Double) {
        viewModelScope.launch {
            repository.updateStorageQuota(targetEmail, newQuotaGb)
            repository.logAction(
                action = "STORAGE_QUOTA_UPDATED",
                detail = "Owner updated storage quota of $targetEmail to $newQuotaGb GB",
                deviceInfo = "Cloud Console",
                ip = "103.114.98.24",
                success = true
            )
        }
    }

    // Cloud file management
    fun uploadCloudFile(fileName: String, fileType: String, sizeMb: Double, snippet: String = "") {
        val userEmail = _authUiState.value.currentUser?.email ?: _authUiState.value.email
        val fileId = UUID.randomUUID().toString().take(8)
        val newFile = CloudFile(
            id = UUID.randomUUID().toString(),
            userEmail = userEmail,
            fileName = fileName,
            fileType = fileType,
            sizeMb = sizeMb,
            shareLink = "https://www.REDTEXT.com.BD/file/$fileId",
            contentSnippet = snippet,
            isEncrypted = true,
            uploadedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.insertCloudFile(newFile)
            // Refresh currentUser to update used storage display
            val updatedUser = repository.getUserByEmail(userEmail)
            _authUiState.value = _authUiState.value.copy(currentUser = updatedUser)
            repository.logAction(
                action = "FILE_UPLOAD",
                detail = "Uploaded '$fileName' (${sizeMb} MB) to www.REDTEXT.com.BD cloud",
                deviceInfo = _authUiState.value.deviceName,
                ip = "103.114.98.24",
                success = true
            )
        }
    }

    fun deleteCloudFile(file: CloudFile) {
        viewModelScope.launch {
            repository.deleteCloudFile(file)
            val updatedUser = repository.getUserByEmail(file.userEmail)
            _authUiState.value = _authUiState.value.copy(currentUser = updatedUser)
            repository.logAction(
                action = "FILE_DELETE",
                detail = "Removed file '${file.fileName}' from cloud storage",
                deviceInfo = _authUiState.value.deviceName,
                ip = "103.114.98.24",
                success = true
            )
        }
    }

    fun toggleRevealContent(id: Long, item: RedtextItem) {
        val current = _revealedItemIds.value
        if (current.contains(id)) {
            _revealedItemIds.value = current - id
        } else {
            _revealedItemIds.value = current + id
            if (item.isBurnAfterReading) {
                viewModelScope.launch {
                    val burned = repository.handleBurnView(item)
                    if (burned) {
                        _revealedItemIds.value = _revealedItemIds.value - id
                    }
                }
            }
        }
    }

    fun decryptItemContent(encryptedContent: String): String {
        return SecurityManager.decryptText(encryptedContent)
    }

    fun createItem(
        title: String,
        content: String,
        category: String,
        tags: String,
        isBurnAfterReading: Boolean,
        burnViews: Int
    ) {
        viewModelScope.launch {
            val item = RedtextItem(
                title = title.trim(),
                content = content.trim(),
                category = category,
                tags = tags.trim(),
                isFavorite = false,
                isBurnAfterReading = isBurnAfterReading,
                burnViewsRemaining = if (isBurnAfterReading) burnViews else 1
            )
            repository.insertItem(item)
        }
    }

    fun toggleFavorite(item: RedtextItem) {
        viewModelScope.launch {
            repository.updateItem(item.copy(isFavorite = !item.isFavorite))
        }
    }

    fun deleteItem(item: RedtextItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun pairNewDevice(name: String, type: String) {
        viewModelScope.launch {
            val newDevice = VerifiedDevice(
                id = UUID.randomUUID().toString(),
                deviceName = name,
                deviceType = type,
                fingerprintHash = "SHA256:${UUID.randomUUID().toString().take(8).uppercase()}...${UUID.randomUUID().toString().take(4).uppercase()}",
                ipAddress = "103.114.98.${(10..240).random()}",
                status = "VERIFIED",
                isCurrentDevice = false,
                lastActiveTime = System.currentTimeMillis(),
                registeredAt = System.currentTimeMillis()
            )
            repository.insertDevice(newDevice)
        }
    }

    fun revokeDevice(device: VerifiedDevice) {
        viewModelScope.launch {
            repository.updateDeviceStatus(device.id, "REVOKED")
        }
    }

    fun deleteDevice(device: VerifiedDevice) {
        viewModelScope.launch {
            repository.deleteDevice(device.id)
        }
    }

    fun resetVault() {
        viewModelScope.launch {
            repository.wipeAllData()
            _authUiState.value = AuthUiState(
                stage = AuthStage.EMAIL_INPUT,
                email = "redwanliton9@gmail.com",
                deviceFingerprint = SecurityManager.getDeviceFingerprint(context),
                deviceName = SecurityManager.getDeviceName()
            )
        }
    }
}
