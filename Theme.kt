package com.musicnova.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Pure black + dusty rose, like the LastWave screenshots. */
object NovaColors {
    val Bg = Color(0xFF000000)
    val BgSoft = Color(0xFF0B0909)
    val Card = Color(0xFF141111)
    val CardHigh = Color(0xFF241D1D)
    val Chip = Color(0xFF1B1717)
    val Rose = Color(0xFFE6C8C8)
    val OnRose = Color(0xFF3F2A2A)
    val RoseDim = Color(0xFF3E3030)
    val Accent = Color(0xFFB79CFF)
    val Outline = Color(0x33FFFFFF)
    val OutlineSoft = Color(0x1FFFFFFF)
    val TextPrimary = Color(0xFFF7F2F2)
    val TextSecondary = Color(0xFFB5ABAB)
    val TextMuted = Color(0xFF7D7373)
    val Scrim = Color(0xD90C0A0A)
    val Heart = Color(0xFFFF8FA3)
}

object NovaBrush {
    val header = Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF0E0B0B)))
    val hero = Brush.linearGradient(listOf(Color(0xFF3A302B), Color(0xFF0D0B0A)))
}

/** Small uppercase label, e.g. "MADE FOR YOU". */
val LabelCaps = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    letterSpacing = 1.6.sp
)

private val NovaTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold,
        fontSize = 40.sp, letterSpacing = (-0.8).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold,
        fontSize = 36.sp, letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, letterSpacing = (-0.3).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 17.sp
    ),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 12.sp
    ),
)

@Composable
fun NovaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = NovaColors.Rose,
            onPrimary = NovaColors.OnRose,
            secondary = NovaColors.Accent,
            background = NovaColors.Bg,
            onBackground = NovaColors.TextPrimary,
            surface = NovaColors.Card,
            onSurface = NovaColors.TextPrimary,
            surfaceVariant = NovaColors.CardHigh,
            onSurfaceVariant = NovaColors.TextSecondary,
        ),
        typography = NovaTypography,
        content = content
    )
}
