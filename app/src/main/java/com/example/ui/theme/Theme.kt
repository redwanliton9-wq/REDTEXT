package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RedtextDarkColorScheme = darkColorScheme(
    primary = CrimsonRed,
    onPrimary = TextWhite,
    primaryContainer = CrimsonContainer,
    onPrimaryContainer = CrimsonRedLight,
    secondary = CrimsonRedLight,
    onSecondary = PitchBlack,
    secondaryContainer = BlackCardElevated,
    onSecondaryContainer = CrimsonRedLight,
    tertiary = VerifiedGreen,
    onTertiary = PitchBlack,
    tertiaryContainer = VerifiedGreenContainer,
    onTertiaryContainer = VerifiedGreen,
    background = PitchBlack,
    onBackground = TextWhite,
    surface = BlackSurface,
    onSurface = TextWhite,
    surfaceVariant = BlackCard,
    onSurfaceVariant = TextGray,
    outline = BlackCardBorder,
    outlineVariant = CrimsonContainer
)

@Composable
fun RedtextTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RedtextDarkColorScheme,
        typography = Typography,
        content = content
    )
}
