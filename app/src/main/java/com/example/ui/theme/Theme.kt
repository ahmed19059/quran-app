package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class QuranThemeMode(val titleAr: String, val bgPreview: Color) {
    DARK("النمط الليلي الفاخر (مريح للعين)", DarkQuranBg),
    LIGHT("النمط الصباحي المشرق", Color(0xFFF9F7F2)),
    EMERALD("الأخضر الزيتي الملكي", Color(0xFF0F1C18)),
    SEPIA("الورق العتيق الدافئ", ParchmentBg)
}

private val LightMorningColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4EEDF),
    onPrimaryContainer = Color(0xFF003826),
    secondary = Color(0xFF8D6E14),
    onSecondary = Color.White,
    background = Color(0xFFF9F7F2),
    onBackground = Color(0xFF1B2E24),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B2E24),
    surfaceVariant = Color(0xFFEFECE4),
    onSurfaceVariant = Color(0xFF495550),
    outline = Color(0xFFCBD5D0)
)

private val DarkQuranColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = DarkQuranBg,
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = Color.White,
    secondary = GoldLight,
    onSecondary = DarkQuranBg,
    background = DarkQuranBg,
    onBackground = DarkQuranText,
    surface = DarkQuranSurface,
    onSurface = DarkQuranText,
    surfaceVariant = DarkQuranCard,
    onSurfaceVariant = DarkQuranSecondaryText,
    outline = DarkQuranBorder
)

private val EmeraldRoyalColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    background = Color(0xFF0A1411),
    onBackground = DarkQuranText,
    surface = Color(0xFF101F1A),
    onSurface = DarkQuranText,
    surfaceVariant = Color(0xFF162923),
    onSurfaceVariant = DarkQuranSecondaryText,
    outline = Color(0xFF223E35)
)

private val SepiaColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color.Black,
    primaryContainer = ParchmentSurface,
    onPrimaryContainer = ParchmentText,
    secondary = Color(0xFFD4B182),
    onSecondary = Color.Black,
    background = ParchmentBg,
    onBackground = ParchmentText,
    surface = ParchmentSurface,
    onSurface = ParchmentText,
    surfaceVariant = Color(0xFF2E2920),
    onSurfaceVariant = Color(0xFFC4B69E),
    outline = ParchmentBorder
)

@Composable
fun QuranAppTheme(
    themeMode: QuranThemeMode = QuranThemeMode.DARK,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        QuranThemeMode.DARK -> DarkQuranColorScheme
        QuranThemeMode.LIGHT -> LightMorningColorScheme
        QuranThemeMode.EMERALD -> EmeraldRoyalColorScheme
        QuranThemeMode.SEPIA -> SepiaColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

