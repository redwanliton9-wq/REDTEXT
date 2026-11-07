package com.example.security

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.CancellationSignal
import android.util.Log

enum class BiometricStatus {
    AVAILABLE,
    NO_HARDWARE,
    NONE_ENROLLED,
    UNSUPPORTED
}

object BiometricAuthHelper {
    private const val TAG = "BiometricAuthHelper"

    /**
     * Checks if biometric hardware and enrollment are present on the host device.
     */
    fun checkBiometricStatus(context: Context): BiometricStatus {
        val packageManager = context.packageManager

        val hasFingerprint = packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
        val hasFace = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
        } else {
            false
        }

        if (!hasFingerprint && !hasFace) {
            return BiometricStatus.NO_HARDWARE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val biometricManager = context.getSystemService(android.hardware.biometrics.BiometricManager::class.java)
                if (biometricManager != null) {
                    val canAuth = biometricManager.canAuthenticate()
                    return when (canAuth) {
                        android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
                        android.hardware.biometrics.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NONE_ENROLLED
                        android.hardware.biometrics.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
                        else -> BiometricStatus.AVAILABLE
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "BiometricManager check error: ${e.message}")
            }
        }

        return BiometricStatus.AVAILABLE
    }

    /**
     * Attempts to trigger the Android system BiometricPrompt (API 28+).
     * If the system prompt is dismissed, cancelled, or fails, returns false with details.
     */
    fun authenticateWithSystemBiometrics(
        activity: Activity,
        title: String = "Redtext Vault Authentication",
        subtitle: String = "Touch fingerprint sensor or use Face Unlock",
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ): CancellationSignal? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            onResult(false, "System biometrics require Android 9.0 (API 28) or higher")
            return null
        }

        val cancellationSignal = CancellationSignal()

        try {
            val executor = activity.mainExecutor
            val prompt = android.hardware.biometrics.BiometricPrompt.Builder(activity)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription("Verify your biometric identity to access www.REDTEXT.com.BD")
                .setNegativeButton("Use Password / OTP", executor) { _, _ ->
                    onResult(false, "Authentication cancelled by user")
                }
                .build()

            prompt.authenticate(
                cancellationSignal,
                executor,
                object : android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: android.hardware.biometrics.BiometricPrompt.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        onResult(true, null)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                        // If user canceled, don't show harsh error
                        val msg = errString?.toString() ?: "Biometric error code $errorCode"
                        onResult(false, msg)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onResult(false, "Biometric not recognized. Please retry.")
                    }
                }
            )
            return cancellationSignal
        } catch (e: Exception) {
            Log.e(TAG, "System biometric prompt launch failed: ${e.message}", e)
            onResult(false, e.message ?: "Failed to initialize biometric hardware")
            return null
        }
    }
}
