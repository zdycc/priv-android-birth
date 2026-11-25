package com.birthdate.app.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.birthdate.app.R
import com.birthdate.app.data.FriendRepository
import com.birthdate.app.logic.BirthdayCalculator
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object BirthdayNotifications {

    private const val CHANNEL_ID = "birthday_reminders"
    private const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "生日提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "好友生日当天与前一天提醒" }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    fun schedule(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = nextTriggerAtMillis(hour, minute)
        if (isExactAlarmAllowed(context)) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun isExactAlarmAllowed(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()

    fun checkAndNotify(context: Context) {
        createChannel(context)
        val repository = FriendRepository(context)
        val friends = repository.friends.value
        val today = LocalDate.now()

        val todayNames = friends.filter {
            BirthdayCalculator.nextBirthday(it, today).daysUntil == 0
        }.map { it.name }

        val tomorrowNames = friends.filter {
            BirthdayCalculator.nextBirthday(it, today).daysUntil == 1
        }.map { it.name }

        val tomorrow = today.plusDays(1)
        val lines = buildList {
            if (todayNames.isNotEmpty()) {
                add("今天（${today.monthValue}月${today.dayOfMonth}日）生日：${todayNames.joinToString("、")}")
            }
            if (tomorrowNames.isNotEmpty()) {
                add("明天（${tomorrow.monthValue}月${tomorrow.dayOfMonth}日）生日：${tomorrowNames.joinToString("、")}")
            }
        }
        if (lines.isEmpty()) return

        val text = lines.joinToString("\n")
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("生日提醒")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        }
    }

    private fun nextTriggerAtMillis(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        var target = now.toLocalDate().atTime(hour, minute)
        if (!target.isAfter(now)) target = target.plusDays(1)
        return target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
