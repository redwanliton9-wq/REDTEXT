package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlackCard
import com.example.ui.theme.BlackCardBorder
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedLight
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer

@Composable
fun WebBrowserPortalScreen(
    currentEmail: String,
    isOwner: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    val officialDomain = "www.REDTEXT.com.BD"
    val fullUrl = "https://www.REDTEXT.com.BD"

    var isReloading by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Browser Header Frame
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CrimsonContainer, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BlackCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Browser Window Controls & URL bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PitchBlack)
                            .border(1.dp, BlackCardBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Traffic lights
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CrimsonRed))
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TextMuted))
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(VerifiedGreen))
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "SSL Secure",
                                tint = VerifiedGreen,
                                modifier = Modifier.size(13.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = fullUrl,
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(fullUrl))
                                    Toast.makeText(context, "Copied URL: $fullUrl", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = TextWhite,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    isReloading = true
                                    Toast.makeText(context, "Refreshed $officialDomain CDN", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reload",
                                    tint = CrimsonRedLight,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(VerifiedGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DNS: BD Root Server (Dhaka IXP)",
                                color = TextGray,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "Latency: 8ms • 60 FPS Smooth",
                            color = VerifiedGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Web Access Any Browser Instructions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BlackCardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = BlackCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CrimsonContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = CrimsonRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Access from Any Browser with Internet",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Chrome • Firefox • Safari • Edge • Brave",
                                color = CrimsonRedLight,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Redtext is engineered to run seamlessly inside any web browser without needing to download an application. Any person around the world can open their browser and access their cloud data immediately.",
                        color = TextGray,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    WebFeatureStepRow(
                        step = "1",
                        title = "Type URL in Browser",
                        desc = "Navigate to www.REDTEXT.com.BD on any device."
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    WebFeatureStepRow(
                        step = "2",
                        title = "Email + Password Login",
                        desc = "Sign in with your email and password credentials."
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    WebFeatureStepRow(
                        step = "3",
                        title = "OTP Verification",
                        desc = "Enter the 6-digit one-time cryptographic passcode."
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    WebFeatureStepRow(
                        step = "4",
                        title = "Cloud Storage Allocated",
                        desc = "Initial 5GB cloud data storage unlocked instantly (Owner gets 500GB)."
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Owner & Multi-User Architecture Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BlackCardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = BlackCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CLOUD STORAGE SPECIFICATION",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Main Owner", color = TextGray, fontSize = 11.sp)
                            Text(text = "redwanliton9@gmail.com", color = CrimsonRedLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Quota: 500 GB Storage", color = VerifiedGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Column {
                            Text(text = "Other People", color = TextGray, fontSize = 11.sp)
                            Text(text = "Any Verified Person", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Quota: 5 GB Initial", color = TextGray, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Storage quotas can be upgraded day to day directly by the owner (Redwan) through the Owner Console tab.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WebFeatureStepRow(
    step: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PitchBlack)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(CrimsonContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                color = CrimsonRedLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(text = title, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = desc, color = TextGray, fontSize = 11.sp)
        }
    }
}
