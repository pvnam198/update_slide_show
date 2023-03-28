package com.example.slide.notify

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.slide.notify.NotificationConfig.NOTIFICATION_ID
import java.util.*

class NotificationSetter constructor(val notificationRepository: NotificationRepository) {

    companion object {

        private const val EXPAND_YEARS = 1

        private const val THREE_DAYS_MILLI = 3 * 86400000

        private const val MINUTE_OF_ONE_HOUR = 60L

        private const val OK_TIME_1 = 9

        private const val OK_TIME_2 = 12

        private const val OK_TIME_3 = 17

        private const val OK_TIME_4 = 19

    }

    private lateinit var calendar: Calendar

    fun schedule(context: Context) {
        setAlarm(context, createAlarmPendingIntent(context))
    }

    private fun setCalendarToAlarmTime() {
        try {
            calendar = Calendar.getInstance(Locale.getDefault())
            calendar.apply {
                set(Calendar.DAY_OF_YEAR, getValidDayOfYear(3))
                set(Calendar.HOUR_OF_DAY, notificationRepository.getAlarmHour())
                set(Calendar.MINUTE, notificationRepository.getAlarmMinute())
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                validTime()
                Log.d("log_time_notify", "setCalendarToAlarmTime: ${calendar.time}")
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    private fun getValidDayOfYear(expandDays: Int): Int {
        val calendar = Calendar.getInstance()
        val maxDayOfYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
        val tempDayOfYear = calendar.get(Calendar.DAY_OF_YEAR) + expandDays
        return if (tempDayOfYear > maxDayOfYear) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + EXPAND_YEARS)
            tempDayOfYear - maxDayOfYear
        } else {
            tempDayOfYear
        }
    }

    private fun validTime() {
        val currentTime = System.currentTimeMillis()
        val notificationTime = calendar.timeInMillis
        val isOverThreeDay = notificationTime - currentTime > THREE_DAYS_MILLI
        if (isOverThreeDay) {
            val overTimeMinute = (notificationTime - (currentTime + THREE_DAYS_MILLI)) / 600000
            val overTimeHour = (overTimeMinute / MINUTE_OF_ONE_HOUR).toInt()
            val hourRemoteConfig = notificationRepository.getAlarmHour()
            when (hourRemoteConfig - overTimeHour) {
                in 19..23 -> {
                    calendar.set(Calendar.HOUR_OF_DAY, OK_TIME_4)
                }
                in 17..18 -> {
                    calendar.set(Calendar.HOUR_OF_DAY, OK_TIME_3)
                }
                in 12..16 -> {
                    calendar.set(Calendar.HOUR_OF_DAY, OK_TIME_2)
                }
                in 9..11 -> {
                    calendar.set(Calendar.HOUR_OF_DAY, OK_TIME_1)
                }
                in 1..8 -> {
                    calendar.set(Calendar.DAY_OF_YEAR, getValidDayOfYear(2))
                    calendar.set(Calendar.HOUR_OF_DAY, OK_TIME_4)
                }
                else -> {
                    calendar.set(Calendar.DAY_OF_YEAR, getValidDayOfYear(2))
                }
            }
        }
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 3)
        }
    }

    private fun createAlarmPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, NotificationReceive::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }
        return PendingIntent.getBroadcast(
            context, NOTIFICATION_ID, intent, flags
        )
    }

    private fun setAlarm(context: Context, alarmPendingIntent: PendingIntent) {
        setCalendarToAlarmTime()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(
            AlarmClockInfo(calendar.timeInMillis, alarmPendingIntent),
            alarmPendingIntent
        )
    }

}