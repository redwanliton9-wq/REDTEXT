package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ui.components.BiometricPromptDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthStage
import com.example.ui.AuthUiState
import com.example.ui.theme.BlackCard
import com.example.ui.theme.BlackCardBorder
import com.example.ui.theme.BlackCardElevated
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedDark
import com.example.ui.theme.CrimsonRedLight
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer

/**
 * SecureLoginOtpScreen:
 * Secure email and password login UI screen for Redtext that includes an OTP verification step
 * immediately following initial authentication.
 */
@Composable
fun SecureLoginOtpScreen(
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmitCredentials: () -> Unit,
    onOtpCodeChanged: (String) -> Unit,
    onFillSampleOtp: () -> Unit,
    onResendOtp: () -> Unit,
    onBackToCredentials: () -> Unit,
    onRegisterNameChanged: (String) -> Unit = {},
    onToggleRegisterMode: () -> Unit = {},
    onBiometricLogin: (email: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }
    var showBiometricDialog by remember { mutableStateOf(false) }

    val isOtpStep = state.stage == AuthStage.EMAIL_CODE_VERIFY

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Logo & Branding
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(CrimsonRed, CrimsonContainer)
                        )
                    )
                    .border(2.dp, CrimsonRedLight, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Redtext Security Shield",
                    tint = TextWhite,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "RED",
                    color = CrimsonRed,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "TEXT",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }

            Text(
                text = "www.REDTEXT.com.BD",
                color = CrimsonRedLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // FAST BIOMETRIC UNLOCK CARD (Alternative to Email/Password login)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, CrimsonContainer, RoundedCornerShape(16.dp))
                    .clickable { showBiometricDialog = true }
                    .testTag("biometric_quick_login_card"),
                colors = CardDefaults.cardColors(containerColor = BlackCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CrimsonContainer)
                                .border(1.5.dp, CrimsonRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Login",
                                tint = CrimsonRedLight,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Biometric Quick Access",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(VerifiedGreenContainer)
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "FAST PASS",
                                        color = VerifiedGreen,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Fingerprint or Face Unlock • Instant Vault Entry",
                                color = TextGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PitchBlack)
                            .border(1.dp, CrimsonContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Face Unlock",
                            tint = CrimsonRedLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two-Stage Authentication Stepper
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BlackCard)
                    .border(1.dp, BlackCardBorder, RoundedCornerShape(14.dp))
                    .padding(vertical = 10.dp, horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Step 1: Email & Password
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isOtpStep) VerifiedGreenContainer else CrimsonContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isOtpStep) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = VerifiedGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "1",
                                color = CrimsonRedLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Credentials",
                            color = if (isOtpStep) VerifiedGreen else TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Email & Password",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(1.dp)
                        .background(if (isOtpStep) VerifiedGreen else BlackCardBorder)
                )

                // Step 2: OTP Verification
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isOtpStep) CrimsonContainer else PitchBlack)
                            .border(1.dp, if (isOtpStep) CrimsonRed else BlackCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            color = if (isOtpStep) CrimsonRedLight else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "2FA Security",
                            color = if (isOtpStep) CrimsonRedLight else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "OTP Verification",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Interactive Authentication Card with animated switching
            AnimatedContent(
                targetState = isOtpStep,
                transitionSpec = {
                    if (targetState) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "LoginOtpStepAnimation"
            ) { inOtpStep ->
                if (!inOtpStep) {
                    // ==========================================
                    // STEP 1: EMAIL & PASSWORD LOGIN
                    // ==========================================
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BlackCardBorder, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = BlackCard),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Account Authentication",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter email & password. An OTP will be required on the next step.",
                                color = TextGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Email Address Field
                            Text(
                                text = "EMAIL ADDRESS",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = state.email,
                                onValueChange = onEmailChanged,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_email_input"),
                                placeholder = { Text("name@example.com", color = TextMuted, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email",
                                        tint = CrimsonRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CrimsonRed,
                                    unfocusedBorderColor = BlackCardBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    cursorColor = CrimsonRed,
                                    focusedContainerColor = PitchBlack,
                                    unfocusedContainerColor = PitchBlack
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Password Field
                            Text(
                                text = "MASTER PASSWORD",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = state.passwordInput,
                                onValueChange = onPasswordChanged,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input"),
                                placeholder = { Text("Enter password", color = TextMuted, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Password",
                                        tint = CrimsonRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility",
                                            tint = TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CrimsonRed,
                                    unfocusedBorderColor = BlackCardBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    cursorColor = CrimsonRed,
                                    focusedContainerColor = PitchBlack,
                                    unfocusedContainerColor = PitchBlack
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = { onSubmitCredentials() }),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Fast Credential Presets
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PitchBlack)
                                        .border(1.dp, CrimsonContainer, RoundedCornerShape(8.dp))
                                        .clickable {
                                            onEmailChanged("redwanliton9@gmail.com")
                                            onPasswordChanged("password123")
                                        }
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text("Owner (500GB)", color = CrimsonRedLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text("redwanliton9@gmail.com", color = TextMuted, fontSize = 9.sp, maxLines = 1)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PitchBlack)
                                        .border(1.dp, BlackCardBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            onEmailChanged("rashed.chowdhury@gmail.com")
                                            onPasswordChanged("password123")
                                        }
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text("User (5GB)", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text("rashed.chowdhury...", color = TextMuted, fontSize = 9.sp, maxLines = 1)
                                    }
                                }
                            }

                            // Error Display
                            AnimatedVisibility(visible = state.errorMessage != null) {
                                Text(
                                    text = state.errorMessage ?: "",
                                    color = CrimsonRed,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Primary Action Button
                            Button(
                                onClick = onSubmitCredentials,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_credentials_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CrimsonRed,
                                    contentColor = TextWhite
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VpnKey,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Verify Credentials & Get OTP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f).height(1.dp).background(BlackCardBorder))
                                Text(
                                    text = "  OR BIOMETRIC UNLOCK  ",
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(modifier = Modifier.weight(1f).height(1.dp).background(BlackCardBorder))
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedButton(
                                onClick = { showBiometricDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("biometric_fingerprint_face_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonContainer),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = PitchBlack,
                                    contentColor = CrimsonRedLight
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = CrimsonRedLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Touch Fingerprint or Face Scan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = null,
                                        tint = CrimsonRedLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(onClick = onToggleRegisterMode) {
                                Text(
                                    text = if (state.isRegistering) "Already registered? Sign In" else "Create New Person Cloud Account (5GB)",
                                    color = CrimsonRedLight,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                } else {
                    // ==========================================
                    // STEP 2: 2FA OTP VERIFICATION
                    // ==========================================
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CrimsonContainer, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = BlackCard),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Credentials Verified Notice
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VerifiedGreenContainer)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = VerifiedGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Credentials Verified for ${state.email}",
                                    color = VerifiedGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Enter 6-Digit Security OTP",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "A one-time passcode was dispatched to your email address.",
                                color = TextGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Simulated Incoming OTP Banner
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, CrimsonContainer, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = PitchBlack),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = CrimsonRedLight,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Incoming OTP Passcode:",
                                                color = TextMuted,
                                                fontSize = 9.sp
                                            )
                                            Text(
                                                text = "[ ${state.generatedCode} ]",
                                                color = CrimsonRedLight,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace,
                                                letterSpacing = 2.sp
                                            )
                                        }
                                    }

                                    TextButton(
                                        onClick = onFillSampleOtp,
                                        colors = ButtonDefaults.textButtonColors(contentColor = VerifiedGreen)
                                    ) {
                                        Text("AUTO-FILL", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 6-digit Code Input Field
                            OutlinedTextField(
                                value = state.codeInput,
                                onValueChange = onOtpCodeChanged,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_code_input"),
                                placeholder = {
                                    Text(
                                        text = "• • • • • •",
                                        color = TextMuted,
                                        fontSize = 22.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CrimsonRed,
                                    unfocusedBorderColor = BlackCardBorder,
                                    focusedTextColor = CrimsonRedLight,
                                    unfocusedTextColor = TextWhite,
                                    cursorColor = CrimsonRed,
                                    focusedContainerColor = PitchBlack,
                                    unfocusedContainerColor = PitchBlack
                                ),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = 8.sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Error Display
                            AnimatedVisibility(visible = state.errorMessage != null) {
                                Text(
                                    text = state.errorMessage ?: "",
                                    color = CrimsonRed,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Verify OTP Button
                            Button(
                                onClick = {
                                    if (state.codeInput.length == 6) {
                                        // Handled automatically on 6 digits
                                    } else {
                                        onFillSampleOtp()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("confirm_otp_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CrimsonRed,
                                    contentColor = TextWhite
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (state.codeInput.length == 6) "Confirm OTP & Login" else "Auto-Fill Code & Verify",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Footer actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = onResendOtp,
                                    colors = ButtonDefaults.textButtonColors(contentColor = TextGray)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Resend OTP", fontSize = 11.sp)
                                }

                                TextButton(
                                    onClick = onBackToCredentials,
                                    colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Change Credentials", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security Badges Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "🛡 2FA Protected", color = TextGray, fontSize = 11.sp)
                Text(text = "🔒 TLS 1.3 Verified", color = TextGray, fontSize = 11.sp)
                Text(text = "⚡ Zero-Knowledge", color = CrimsonRedLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showBiometricDialog) {
            BiometricPromptDialog(
                initialEmail = state.email,
                onDismiss = { showBiometricDialog = false },
                onAuthenticated = { email ->
                    showBiometricDialog = false
                    onBiometricLogin(email)
                }
            )
        }
    }
}
