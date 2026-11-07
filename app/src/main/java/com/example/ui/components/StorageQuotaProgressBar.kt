package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.util.Locale

/**
 * StorageQuotaProgressBar:
 * Displays an interactive, animated progress bar representing the 5GB initial storage quota,
 * which can be upgraded to 500GB, showing used vs remaining space in real-time.
 */
@Composable
fun StorageQuotaProgressBar(
    usedGb: Double,
    totalStorageGb: Double,
    isOwner: Boolean = false,
    onUpgradeTo500Gb: () -> Unit = {},
    onResetTo5Gb: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val remainingGb = (totalStorageGb - usedGb).coerceAtLeast(0.0)
    val rawRatio = if (totalStorageGb > 0) (usedGb / totalStorageGb).toFloat() else 0f
    val progressTarget = rawRatio.coerceIn(0.02f, 1f)

    // Smooth progress bar animation
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "StorageProgressAnimation"
    )

    val percentUsed = (rawRatio * 100).toInt().coerceIn(0, 100)
    val is500GbTier = totalStorageGb >= 500.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    if (is500GbTier) {
                        listOf(CrimsonRed, VerifiedGreen.copy(alpha = 0.5f), CrimsonContainer)
                    } else {
                        listOf(CrimsonContainer, BlackCardBorder, CrimsonContainer)
                    }
                ),
                shape = RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = BlackCard),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header Row: Tier Badge and Quota Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (is500GbTier) {
                                    Brush.linearGradient(listOf(CrimsonRed, CrimsonContainer))
                                } else {
                                    Brush.linearGradient(listOf(BlackCardElevated, PitchBlack))
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (is500GbTier) Icons.Default.CloudDone else Icons.Default.Storage,
                            contentDescription = "Storage Quota",
                            tint = if (is500GbTier) TextWhite else CrimsonRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (is500GbTier) "500 GB MASTER TIER" else "5 GB INITIAL QUOTA",
                                color = if (is500GbTier) CrimsonRedLight else TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (is500GbTier) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(VerifiedGreenContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "MAX CAPACITY",
                                        color = VerifiedGreen,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Redtext Cloud • www.REDTEXT.com.BD",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                }

                // Percentage Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (percentUsed > 85) CrimsonContainer else PitchBlack)
                        .border(
                            1.dp,
                            if (percentUsed > 85) CrimsonRed else BlackCardBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "$percentUsed% USED",
                        color = if (percentUsed > 85) CrimsonRed else TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Used vs Total vs Remaining Numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Used Space
                Column {
                    Text(
                        text = "USED SPACE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format(Locale.US, "%.2f GB", usedGb),
                        color = CrimsonRedLight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Center: Arrow indicator
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .size(16.dp)
                )

                // Right: Remaining Free Space
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "REMAINING FREE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format(Locale.US, "%.2f GB", remainingGb),
                        color = VerifiedGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom High-Precision Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PitchBlack)
                    .border(1.dp, BlackCardBorder, RoundedCornerShape(8.dp))
            ) {
                // Active fill bar with animated width and crimson gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    CrimsonRedDark,
                                    CrimsonRed,
                                    CrimsonRedLight
                                )
                            )
                        )
                )

                // Segment dividers at 25%, 50%, 75%
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 1..3) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(BlackCard.copy(alpha = 0.6f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend Row: 0 GB | 5 GB (or 500 GB)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0 GB",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (is500GbTier) "250 GB" else "2.5 GB",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = String.format(Locale.US, "Total: %.0f GB", totalStorageGb),
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upgrade Action Area: Switch between 5GB and 500GB
            if (!is500GbTier) {
                // 5GB Mode -> Offers instant upgrade to 500GB
                Button(
                    onClick = onUpgradeTo500Gb,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("upgrade_to_500gb_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonRed,
                        contentColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Upgrade Storage: 5 GB  →  500 GB (100x Space)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                // 500GB Owner Mode -> Shows active status with ability to test 5GB user view
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PitchBlack)
                        .border(1.dp, CrimsonContainer, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = VerifiedGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "500 GB Owner Storage Active",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = onResetTo5Gb,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                    ) {
                        Text(
                            text = "Test 5GB View",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
