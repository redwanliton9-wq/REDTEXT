package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.ui.AuthUiState
import com.example.ui.theme.BlackCard
import com.example.ui.theme.BlackCardBorder
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedLight
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VerifiedGreen

@Composable
fun EmailVerificationScreen(
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRegisterNameChanged: (String) -> Unit,
    onToggleRegisterMode: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Cyber Shield Icon
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(CrimsonRed, CrimsonContainer)
                        )
                    )
                    .border(2.dp, CrimsonRedLight, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Security Shield",
                    tint = TextWhite,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Website Brand
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "RED",
                    color = CrimsonRed,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "TEXT",
                    color = TextWhite,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }

            Text(
                text = "www.REDTEXT.com.BD",
                color = CrimsonRedLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Text(
                text = "Cloud Access Any Data • 5GB User / 500GB Owner",
                color = TextGray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Main Login / Registration Card
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
                    // Step Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CrimsonContainer)
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CrimsonRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.isRegistering) "NEW USER REGISTRATION (5GB FREE)" else "STEP 1: EMAIL & PASSWORD AUTH",
                            color = CrimsonRedLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (state.isRegistering) "Create Cloud Account" else "Sign In to Redtext Cloud",
                        color = TextWhite,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (state.isRegistering) {
                            "Register with your email and password to receive 5GB initial cloud storage."
                        } else {
                            "Access people's cloud data from any browser securely with Email, Password & OTP."
                        },
                        color = TextGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.isRegistering) {
                        // Name input
                        OutlinedTextField(
                            value = state.registerName,
                            onValueChange = onRegisterNameChanged,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Your Full Name", color = TextMuted, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CrimsonRed) },
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
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Email Input Field
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = onEmailChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input_field"),
                        placeholder = { Text("Email Address", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = CrimsonRed)
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password Input Field
                    OutlinedTextField(
                        value = state.passwordInput,
                        onValueChange = onPasswordChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input_field"),
                        placeholder = { Text("Password", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Password", tint = CrimsonRed)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Select identity presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text("Main Owner (500GB)", color = CrimsonRedLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text("Other Person (5GB)", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("rashed.chowdhury...", color = TextMuted, fontSize = 9.sp, maxLines = 1)
                            }
                        }
                    }

                    // Error Message
                    AnimatedVisibility(visible = state.errorMessage != null) {
                        Text(
                            text = state.errorMessage ?: "",
                            color = CrimsonRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button: Email + Password -> OTP
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("verify_email_button"),
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
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verify Credentials & Request OTP",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = onToggleRegisterMode) {
                        Text(
                            text = if (state.isRegistering) "Already registered? Sign In" else "New person? Register for 5GB Cloud Storage",
                            color = CrimsonRedLight,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Trust Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "✓ 5GB Default Storage", color = TextGray, fontSize = 11.sp)
                Text(text = "✓ 500GB Owner Quota", color = CrimsonRedLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "✓ OTP Verified", color = VerifiedGreen, fontSize = 11.sp)
            }
        }
    }
}
