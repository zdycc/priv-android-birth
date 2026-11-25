package com.birthdate.app

import android.app.Application
import com.birthdate.app.data.SettingsStore
import com.birthdate.app.notifications.BirthdayNotifications

class BirthdateApp : Application() {
    override fun onCreate() {
        super.onCreate()
        BirthdayNotifications.createChannel(this)
        val settings = SettingsStore(this)
        BirthdayNotifications.schedule(this, settings.reminderHour, settings.reminderMinute)
    }
}
