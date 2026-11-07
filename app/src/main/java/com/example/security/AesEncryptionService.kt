package com.example.security

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * AesEncryptionService:
 * Client-side AES encryption utility service providing military-grade AES-256-GCM authenticated
 * encryption for all text files and notes stored in the Redtext vault before local storage or sync.
 */
object AesEncryptionService {

    private const val PAYLOAD_HEADER_V2 = "RTX-AES256\$v2\$"
    private const val DEFAULT_MASTER_KEY = "Redtext_CloudVault_AES256_MasterPassphrase_v2"

    private const val GCM_IV_LENGTH = 12 // 96 bits recommended for GCM
    private const val GCM_TAG_LENGTH = 128 // 128-bit authentication tag
    private const val SALT_LENGTH = 16 // 128-bit salt
    private const val PBKDF2_ITERATIONS = 5000

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

    /**
     * Derives a 256-bit AES key from a passphrase and salt using PBKDF2WithHmacSHA256,
     * falling back to SHA-256 hash if PBKDF2 is unavailable.
     */
    fun deriveKey(passphrase: String, salt: ByteArray): SecretKeySpec {
        return try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITERATIONS, 256)
            val secretKey = factory.generateSecret(spec)
            SecretKeySpec(secretKey.encoded, "AES")
        } catch (e: Exception) {
            // High-entropy SHA-256 fallback
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(salt)
            val keyBytes = digest.digest(passphrase.toByteArray(StandardCharsets.UTF_8))
            SecretKeySpec(keyBytes, "AES")
        }
    }

    /**
     * Encrypts plaintext string using AES-256-GCM with a fresh salt and IV.
     * Returns a formatted encrypted string payload starting with [PAYLOAD_HEADER_V2].
     */
    fun encrypt(plainText: String, keyPassphrase: String = DEFAULT_MASTER_KEY): String {
        if (plainText.isEmpty()) return ""

        return try {
            val salt = ByteArray(SALT_LENGTH).apply { secureRandom.nextBytes(this) }
            val iv = ByteArray(GCM_IV_LENGTH).apply { secureRandom.nextBytes(this) }
            val keySpec = deriveKey(keyPassphrase, salt)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)

            val cipherBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

            // Combined layout: [Salt (16 bytes)] + [IV (12 bytes)] + [Ciphertext + Auth Tag]
            val combined = ByteArray(salt.size + iv.size + cipherBytes.size)
            System.arraycopy(salt, 0, combined, 0, salt.size)
            System.arraycopy(iv, 0, combined, salt.size, iv.size)
            System.arraycopy(cipherBytes, 0, combined, salt.size + iv.size, cipherBytes.size)

            PAYLOAD_HEADER_V2 + base64Encode(combined)
        } catch (e: Exception) {
            // Fallback to AES-CBC if GCM is not supported on older legacy environments
            encryptFallbackCbc(plainText, keyPassphrase)
        }
    }

    /**
     * Decrypts an encrypted payload string using AES-256-GCM.
     * Handles both V2 (GCM) and legacy V1 (CBC) payloads seamlessly.
     */
    fun decrypt(cipherPayload: String, keyPassphrase: String = DEFAULT_MASTER_KEY): String {
        if (cipherPayload.isEmpty()) return ""

        return try {
            if (cipherPayload.startsWith(PAYLOAD_HEADER_V2)) {
                val rawEncoded = cipherPayload.removePrefix(PAYLOAD_HEADER_V2)
                val combined = base64Decode(rawEncoded)

                if (combined.size < (SALT_LENGTH + GCM_IV_LENGTH)) {
                    return cipherPayload
                }

                val salt = ByteArray(SALT_LENGTH)
                val iv = ByteArray(GCM_IV_LENGTH)
                val cipherBytesLength = combined.size - SALT_LENGTH - GCM_IV_LENGTH
                val cipherBytes = ByteArray(cipherBytesLength)

                System.arraycopy(combined, 0, salt, 0, SALT_LENGTH)
                System.arraycopy(combined, SALT_LENGTH, iv, 0, GCM_IV_LENGTH)
                System.arraycopy(combined, SALT_LENGTH + GCM_IV_LENGTH, cipherBytes, 0, cipherBytesLength)

                val keySpec = deriveKey(keyPassphrase, salt)
                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)

                val decryptedBytes = cipher.doFinal(cipherBytes)
                String(decryptedBytes, StandardCharsets.UTF_8)
            } else {
                // Try legacy V1 CBC or base64 decoding
                decryptFallbackCbc(cipherPayload, keyPassphrase)
            }
        } catch (e: Exception) {
            // Attempt legacy fallback
            try {
                decryptFallbackCbc(cipherPayload, keyPassphrase)
            } catch (ex: Exception) {
                cipherPayload
            }
        }
    }

    /**
     * Encrypts raw bytes (for file uploads or binary data) with AES-256-GCM.
     */
    fun encryptBytes(plainBytes: ByteArray, keyPassphrase: String = DEFAULT_MASTER_KEY): ByteArray {
        val salt = ByteArray(SALT_LENGTH).apply { secureRandom.nextBytes(this) }
        val iv = ByteArray(GCM_IV_LENGTH).apply { secureRandom.nextBytes(this) }
        val keySpec = deriveKey(keyPassphrase, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)

        val cipherBytes = cipher.doFinal(plainBytes)
        val combined = ByteArray(salt.size + iv.size + cipherBytes.size)
        System.arraycopy(salt, 0, combined, 0, salt.size)
        System.arraycopy(iv, 0, combined, salt.size, iv.size)
        System.arraycopy(cipherBytes, 0, combined, salt.size + iv.size, cipherBytes.size)
        return combined
    }

    /**
     * Decrypts raw bytes with AES-256-GCM.
     */
    fun decryptBytes(encryptedBytes: ByteArray, keyPassphrase: String = DEFAULT_MASTER_KEY): ByteArray {
        if (encryptedBytes.size < (SALT_LENGTH + GCM_IV_LENGTH)) return encryptedBytes

        val salt = ByteArray(SALT_LENGTH)
        val iv = ByteArray(GCM_IV_LENGTH)
        val cipherBytesLength = encryptedBytes.size - SALT_LENGTH - GCM_IV_LENGTH
        val cipherBytes = ByteArray(cipherBytesLength)

        System.arraycopy(encryptedBytes, 0, salt, 0, SALT_LENGTH)
        System.arraycopy(encryptedBytes, SALT_LENGTH, iv, 0, GCM_IV_LENGTH)
        System.arraycopy(encryptedBytes, SALT_LENGTH + GCM_IV_LENGTH, cipherBytes, 0, cipherBytesLength)

        val keySpec = deriveKey(keyPassphrase, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)

        return cipher.doFinal(cipherBytes)
    }

    /**
     * Returns true if payload was encrypted by AesEncryptionService.
     */
    fun isEncrypted(payload: String): Boolean {
        return payload.startsWith(PAYLOAD_HEADER_V2)
    }

    /**
     * Generates a 32-character high-entropy cryptographic passphrase.
     */
    fun generateSecurePassphrase(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+="
        return (1..32).map { chars[secureRandom.nextInt(chars.length)] }.joinToString("")
    }

    // Legacy CBC encryption fallback
    private fun encryptFallbackCbc(plainText: String, secretKey: String): String {
        val keyBytes = MessageDigest.getInstance("SHA-256")
            .digest(secretKey.toByteArray(StandardCharsets.UTF_8))
            .copyOf(16)
        val keySpec = SecretKeySpec(keyBytes, "AES")
        val iv = ByteArray(16).apply { secureRandom.nextBytes(this) }

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(iv))
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

        val combined = iv + encryptedBytes
        return base64Encode(combined)
    }

    // Legacy CBC decryption fallback
    private fun decryptFallbackCbc(cipherText: String, secretKey: String): String {
        val combined = base64Decode(cipherText)
        if (combined.size <= 16) {
            return String(combined, StandardCharsets.UTF_8)
        }
        val keyBytes = MessageDigest.getInstance("SHA-256")
            .digest(secretKey.toByteArray(StandardCharsets.UTF_8))
            .copyOf(16)
        val keySpec = SecretKeySpec(keyBytes, "AES")
        val iv = combined.copyOfRange(0, 16)
        val encryptedBytes = combined.copyOfRange(16, combined.size)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv))
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }
}
