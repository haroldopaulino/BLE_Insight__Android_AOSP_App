package com.harold.ble_insight.wearable.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val Colors = darkColorScheme(
    primary = Color(0xFF55D6D0),
    onPrimary = Color(0xFF003735),
    primaryContainer = Color(0xFF00504D),
    onPrimaryContainer = Color(0xFFA0F2EB),
    secondary = Color(0xFFB79CFF),
    onSecondary = Color(0xFF2E1857),
    secondaryContainer = Color(0xFF46306F),
    onSecondaryContainer = Color(0xFFEADDFF),
    tertiary = Color(0xFF74B9FF),
    background = Color(0xFF090B0F),
    onBackground = Color(0xFFF4F6FA),
    surface = Color(0xFF111419),
    onSurface = Color(0xFFF4F6FA),
    surfaceVariant = Color(0xFF1B1F26),
    onSurfaceVariant = Color(0xFFC5CAD3),
    surfaceContainer = Color(0xFF171B21),
    surfaceContainerHigh = Color(0xFF20252C),
    surfaceContainerHighest = Color(0xFF2A3038),
    outline = Color(0xFF8E949E),
    outlineVariant = Color(0xFF3D434C),
    errorContainer = Color(0xFF5F1D24),
    onErrorContainer = Color(0xFFFFDAD9)
)

private val WearTypography = Typography(
    bodyLarge = TextStyle(fontSize = 14.sp, lineHeight = 18.sp),
    bodyMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    bodySmall = TextStyle(fontSize = 11.sp, lineHeight = 14.sp),
    labelLarge = TextStyle(fontSize = 12.sp, lineHeight = 15.sp),
    labelMedium = TextStyle(fontSize = 11.sp, lineHeight = 14.sp),
    labelSmall = TextStyle(fontSize = 10.sp, lineHeight = 12.sp),
    titleLarge = TextStyle(fontSize = 20.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 21.sp),
    titleSmall = TextStyle(fontSize = 14.sp, lineHeight = 18.sp)
)

@Composable
fun BleInsightWearableTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Colors,
        typography = WearTypography,
        content = content
    )
}
