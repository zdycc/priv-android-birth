package com.birthdate.app.data

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.birthdate.app.ui.theme.DarkMode
import com.birthdate.app.ui.theme.ThemeColor

class SettingsStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _theme = mutableStateOf(
        ThemeColor.valueOf(prefs.getString("theme", ThemeColor.ORANGE.name) ?: ThemeColor.ORANGE.name)
    )
    val theme: ThemeColor get() = _theme.value

    private val _darkMode = mutableStateOf(
        DarkMode.valueOf(prefs.getString("dark_mode", DarkMode.SYSTEM.name) ?: DarkMode.SYSTEM.name)
    )
    val darkMode: DarkMode get() = _darkMode.value

    private val _reminderHour = mutableStateOf(prefs.getInt("reminder_hour", 9))
    val reminderHour: Int get() = _reminderHour.value

    private val _reminderMinute = mutableStateOf(prefs.getInt("reminder_minute", 0))
    val reminderMinute: Int get() = _reminderMinute.value

    fun updateTheme(value: ThemeColor) {
        _theme.value = value
        prefs.edit().putString("theme", value.name).apply()
    }

    fun updateDarkMode(value: DarkMode) {
        _darkMode.value = value
        prefs.edit().putString("dark_mode", value.name).apply()
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        _reminderHour.value = hour
        _reminderMinute.value = minute
        prefs.edit().putInt("reminder_hour", hour).putInt("reminder_minute", minute).apply()
    }
}
