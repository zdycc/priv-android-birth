package com.birthdate.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class ThemeColor(val label: String) {
    ORANGE("橙色"),
    BLUE("蓝色"),
    PINK("粉色"),
    GREEN("绿色")
}

enum class DarkMode(val label: String) {
    SYSTEM("跟随系统"),
    LIGHT("浅色"),
    DARK("深色")
}

private data class Palette(
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val background: Color,
    val onSurfaceVariant: Color
)

private val LightPalettes = mapOf(
    ThemeColor.ORANGE to Palette(
        Color(0xFFFB923C), Color(0xFFFFEDD5), Color(0xFF431407),
        Color(0xFFFFFBF7), Color(0xFF78716C)
    ),
    ThemeColor.BLUE to Palette(
        Color(0xFF3B82F6), Color(0xFFDBEAFE), Color(0xFF1E3A8A),
        Color(0xFFF8FAFC), Color(0xFF64748B)
    ),
    ThemeColor.PINK to Palette(
        Color(0xFFE0567A), Color(0xFFFFD9E0), Color(0xFF4A1622),
        Color(0xFFFFF7F6), Color(0xFF8A6E74)
    ),
    ThemeColor.GREEN to Palette(
        Color(0xFF10B981), Color(0xFFD1FAE5), Color(0xFF064E3B),
        Color(0xFFF7FCFA), Color(0xFF64748B)
    )
)

private val DarkPalettes = mapOf(
    ThemeColor.ORANGE to Palette(
        Color(0xFFFDBA74), Color(0xFF7C2D12), Color(0xFFFFEDD5),
        Color(0xFF1C1917), Color(0xFFA8A29E)
    ),
    ThemeColor.BLUE to Palette(
        Color(0xFF93C5FD), Color(0xFF1E40AF), Color(0xFFDBEAFE),
        Color(0xFF0F172A), Color(0xFF94A3B8)
    ),
    ThemeColor.PINK to Palette(
        Color(0xFFFFB1C3), Color(0xFF5C2434), Color(0xFFFFD9E0),
        Color(0xFF1C1215), Color(0xFFC9AAB1)
    ),
    ThemeColor.GREEN to Palette(
        Color(0xFF6EE7B7), Color(0xFF064E3B), Color(0xFFD1FAE5),
        Color(0xFF0B1D17), Color(0xFF94A3B8)
    )
)

fun themeColorScheme(theme: ThemeColor, darkTheme: Boolean): ColorScheme {
    val p = (if (darkTheme) DarkPalettes else LightPalettes)[theme] ?: LightPalettes[ThemeColor.ORANGE]!!
    return if (darkTheme) {
        darkColorScheme(
            primary = p.primary,
            onPrimary = Color(0xFF1E293B),
            primaryContainer = p.primaryContainer,
            onPrimaryContainer = p.onPrimaryContainer,
            secondary = p.primary,
            background = p.background,
            onBackground = Color(0xFFF5F5F4),
            surface = Color(0xFF1E293B),
            onSurface = Color(0xFFF5F5F4),
            onSurfaceVariant = p.onSurfaceVariant,
            surfaceVariant = p.primaryContainer
        )
    } else {
        lightColorScheme(
            primary = p.primary,
            onPrimary = Color.White,
            primaryContainer = p.primaryContainer,
            onPrimaryContainer = p.onPrimaryContainer,
            secondary = p.primary,
            background = p.background,
            onBackground = Color(0xFF292524),
            surface = Color.White,
            onSurface = Color(0xFF292524),
            onSurfaceVariant = p.onSurfaceVariant,
            surfaceVariant = p.primaryContainer
        )
    }
}
