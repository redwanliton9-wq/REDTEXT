package com.example

import com.example.security.AesEncryptionService
import com.example.security.SecurityManager
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun securityManager_generatesSixDigitCode() {
    val code = SecurityManager.generateVerificationCode()
    assertEquals(6, code.length)
    assertTrue(code.all { it.isDigit() })
  }

  @Test
  fun securityManager_encryptAndDecrypt() {
    val original = "Redtext_Secret_Key_Secure_123"
    val encrypted = SecurityManager.encryptText(original)
    assertNotEquals(original, encrypted)
    val decrypted = SecurityManager.decryptText(encrypted)
    assertEquals(original, decrypted)
  }

  @Test
  fun aesEncryptionService_encryptsAndDecryptsWithGcm() {
    val sensitiveText = "Confidential Vault Data 2026 - Master Secret Code 9942"
    val cipherText = AesEncryptionService.encrypt(sensitiveText)

    assertTrue(AesEncryptionService.isEncrypted(cipherText))
    assertNotEquals(sensitiveText, cipherText)

    val decrypted = AesEncryptionService.decrypt(cipherText)
    assertEquals(sensitiveText, decrypted)
  }

  @Test
  fun aesEncryptionService_usesRandomIv_producesDistinctCiphertexts() {
    val plain = "Same text across multiple saves"
    val enc1 = AesEncryptionService.encrypt(plain)
    val enc2 = AesEncryptionService.encrypt(plain)

    // Even for identical plaintext, fresh IV/salt generates distinct ciphertexts
    assertNotEquals(enc1, enc2)

    assertEquals(plain, AesEncryptionService.decrypt(enc1))
    assertEquals(plain, AesEncryptionService.decrypt(enc2))
  }

  @Test
  fun aesEncryptionService_customPassphrase() {
    val plain = "Highly confidential client document"
    val customKey = "MySuperSecretCustomKeyPassphrase!9988"

    val encrypted = AesEncryptionService.encrypt(plain, customKey)
    val decrypted = AesEncryptionService.decrypt(encrypted, customKey)
    assertEquals(plain, decrypted)

    // Decrypting with wrong key should not produce original plain text
    val wrongDecrypted = AesEncryptionService.decrypt(encrypted, "WrongKeyPassphrase123")
    assertNotEquals(plain, wrongDecrypted)
  }

  @Test
  fun aesEncryptionService_encryptAndDecryptRawBytes() {
    val sampleFileBytes = "Simulated binary file content \u0000\u0001\u0002".toByteArray()
    val encryptedBytes = AesEncryptionService.encryptBytes(sampleFileBytes)

    assertFalse(sampleFileBytes.contentEquals(encryptedBytes))

    val decryptedBytes = AesEncryptionService.decryptBytes(encryptedBytes)
    assertArrayEquals(sampleFileBytes, decryptedBytes)
  }

  @Test
  fun aesEncryptionService_generatesSecurePassphrase() {
    val pass = AesEncryptionService.generateSecurePassphrase()
    assertEquals(32, pass.length)
  }

  @Test
  fun cloudFile_searchFilteringByNameAndContentSnippet() {
    val files = listOf(
      com.example.data.entity.CloudFile(
        id = "f1",
        userEmail = "owner@redtext.com.bd",
        fileName = "Production_Database_Secrets.enc",
        fileType = "DATA",
        sizeMb = 12.4,
        shareLink = "https://www.REDTEXT.com.BD/file/1",
        contentSnippet = "AES-256-GCM encrypted database dump and replication seed keys",
        isEncrypted = true,
        uploadedAt = 1000L
      ),
      com.example.data.entity.CloudFile(
        id = "f2",
        userEmail = "owner@redtext.com.bd",
        fileName = "Server_TLS_Certificates.tar.gz",
        fileType = "ARCHIVE",
        sizeMb = 4.2,
        shareLink = "https://www.REDTEXT.com.BD/file/2",
        contentSnippet = "Root CA certificate chain, wildcard certs for *.redtext.com.bd",
        isEncrypted = true,
        uploadedAt = 2000L
      ),
      com.example.data.entity.CloudFile(
        id = "f3",
        userEmail = "owner@redtext.com.bd",
        fileName = "Personal_Journal_2026.txt",
        fileType = "DOC",
        sizeMb = 0.5,
        shareLink = "https://www.REDTEXT.com.BD/file/3",
        contentSnippet = "Encrypted thoughts, emergency contacts, and hardware token pin",
        isEncrypted = true,
        uploadedAt = 3000L
      )
    )

    // Search by name snippet "TLS"
    val tlsMatches = files.filter { it.fileName.contains("TLS", ignoreCase = true) || it.contentSnippet.contains("TLS", ignoreCase = true) }
    assertEquals(1, tlsMatches.size)
    assertEquals("Server_TLS_Certificates.tar.gz", tlsMatches[0].fileName)

    // Search by content snippet "seed keys"
    val seedMatches = files.filter { it.fileName.contains("seed keys", ignoreCase = true) || it.contentSnippet.contains("seed keys", ignoreCase = true) }
    assertEquals(1, seedMatches.size)
    assertEquals("Production_Database_Secrets.enc", seedMatches[0].fileName)

    // Search by content snippet "hardware token"
    val tokenMatches = files.filter { it.fileName.contains("hardware token", ignoreCase = true) || it.contentSnippet.contains("hardware token", ignoreCase = true) }
    assertEquals(1, tokenMatches.size)
    assertEquals("Personal_Journal_2026.txt", tokenMatches[0].fileName)

    // Case-insensitive query "wildcard"
    val wildcardMatches = files.filter { it.fileName.contains("wildcard", ignoreCase = true) || it.contentSnippet.contains("wildcard", ignoreCase = true) }
    assertEquals(1, wildcardMatches.size)
    assertEquals("Server_TLS_Certificates.tar.gz", wildcardMatches[0].fileName)

    // Empty search matches all
    val query = ""
    val allMatches = files.filter { query.isBlank() || it.fileName.contains(query, ignoreCase = true) || it.contentSnippet.contains(query, ignoreCase = true) }
    assertEquals(3, allMatches.size)
  }
}
