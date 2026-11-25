package com.birthdate.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.birthdate.app.data.SettingsStore

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        BirthdayNotifications.checkAndNotify(context)
        val settings = SettingsStore(context)
        BirthdayNotifications.schedule(context, settings.reminderHour, settings.reminderMinute)
    }
}

class RescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val settings = SettingsStore(context)
        BirthdayNotifications.schedule(context, settings.reminderHour, settings.reminderMinute)
    }
}
