package com.birthdate.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import com.birthdate.app.data.SettingsStore
import com.birthdate.app.ui.AppRoot
import com.birthdate.app.ui.theme.BirthdateTheme
import com.birthdate.app.ui.theme.DarkMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsStore = remember { SettingsStore(applicationContext) }
            BirthdateTheme(
                theme = settingsStore.theme,
                darkTheme = when (settingsStore.darkMode) {
                    DarkMode.SYSTEM -> isSystemInDarkTheme()
                    DarkMode.LIGHT -> false
                    DarkMode.DARK -> true
                }
            ) {
                AppRoot(settingsStore)
            }
        }
    }
}
