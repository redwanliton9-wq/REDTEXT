package com.example.security

import android.content.Context
import android.os.Build
import android.provider.Settings
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object SecurityManager {

    private const val FIXED_SALT = "Redtext_AES256_VaultKey_v1"
    private val secureRandom = SecureRandom()

    // Base64 helper safe for both JVM unit tests and Android runtime
    private fun base64Encode(bytes: ByteArray): String {
        return try {
            java.util.Base64.getEncoder().encodeToString(bytes)
        } catch (t: Throwable) {
            android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
        }
    }

    private fun base64Decode(str: String): ByteArray {
        return try {
            java.util.Base64.getDecoder().decode(str)
        } catch (t: Throwable) {
            android.util.Base64.decode(str, android.util.Base64.NO_WRAP)
        }
    }

    // Generate a human-readable 6-digit security verification code
    fun generateVerificationCode(): String {
        val code = 100000 + secureRandom.nextInt(900000)
        return code.toString()
    }

    // Generate a cryptographic hardware fingerprint for this device
    fun getDeviceFingerprint(context: Context): String {
        return try {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "unknown_id"
            val raw = "${Build.MANUFACTURER}_${Build.MODEL}_${Build.HARDWARE}_$androidId"
            val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray(StandardCharsets.UTF_8))
            val hex = digest.joinToString("") { "%02X".format(it) }
            "SHA256:${hex.take(16)}...${hex.takeLast(6)}"
        } catch (e: Exception) {
            "SHA256:F4B27C89...99A1"
        }
    }

    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL
        return if (model.startsWith(manufacturer, ignoreCase = true)) {
            model
        } else {
            "$manufacturer $model"
        }
    }

    // Encrypt content with client-side AES-256-GCM
    fun encryptText(plainText: String, secretKey: String = FIXED_SALT): String {
        return AesEncryptionService.encrypt(plainText, secretKey)
    }

    // Decrypt content with client-side AES-256-GCM
    fun decryptText(cipherText: String, secretKey: String = FIXED_SALT): String {
        return AesEncryptionService.decrypt(cipherText, secretKey)
    }
}
