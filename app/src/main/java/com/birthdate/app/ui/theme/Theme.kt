package com.birthdate.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun BirthdateTheme(
    theme: ThemeColor = ThemeColor.ORANGE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = themeColorScheme(theme, darkTheme),
        content = content
    )
}
