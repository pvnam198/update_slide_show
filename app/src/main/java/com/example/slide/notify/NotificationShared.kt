package com.example.slide.notify

import android.content.Context
import android.content.SharedPreferences
import com.example.slide.R
import com.example.slide.ads.Ads

class NotificationShared constructor(private val context: Context) {

    companion object {

        private const val PREFERENCE_FILE_KEY = "NOTIFICATION_PREFERENCES_KEY"

        private const val PREFERENCES_ALARM_HOUR_KEY = "alarmHour"

        private const val PREFERENCES_ALARM_MINUTE_KEY = "alarmMinute"

        private const val PREFERENCES_NOTIFICATION_CONTENT = "notificationContent"

        private const val PREFERENCES_NOTIFICATION_APPEARS_KEY = "notification_appears"

        private const val PREFERENCES_SCHEDULED = "preferences_scheduled"

    }

    private val threeDaysMilli = 3 * 86400000

    private val milliseconds6Hours = 6 * 3600000

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)

    fun setNotificationTime(alarmHour: Int, alarmMinute: Int) {
        sharedPreferences.edit().apply {
            putInt(PREFERENCES_ALARM_HOUR_KEY, alarmHour)
            putInt(PREFERENCES_ALARM_MINUTE_KEY, alarmMinute)
            apply()
        }
    }

    fun setRemoteNotificationContent(content: Int) {
        sharedPreferences.edit().putInt(PREFERENCES_NOTIFICATION_CONTENT, content).apply()
    }

    fun getAlarmHour() =
        sharedPreferences.getInt(PREFERENCES_ALARM_HOUR_KEY, Ads.notification_setup_h)

    fun getAlarmMinute() =
        sharedPreferences.getInt(PREFERENCES_ALARM_MINUTE_KEY, Ads.notification_setup_m)

    private fun getTimeNotificationAppears(): Long {
        return sharedPreferences.getLong(PREFERENCES_NOTIFICATION_APPEARS_KEY, 0)
    }

    private fun getScheduledTime(): Long = sharedPreferences.getLong(PREFERENCES_SCHEDULED, 0)

    fun shouldCreateNotify(): Boolean {
        val shouldScheduled =
            System.currentTimeMillis() - getScheduledTime() > threeDaysMilli
        if (shouldScheduled)
            sharedPreferences.edit().putLong(PREFERENCES_SCHEDULED, System.currentTimeMillis()).apply()
        return shouldScheduled
    }

    private fun setTimeNotificationAppears() {
        sharedPreferences.edit().apply {
            putLong(PREFERENCES_NOTIFICATION_APPEARS_KEY, System.currentTimeMillis())
            apply()
        }
    }

    fun shouldReceive(): Boolean {
        val should = System.currentTimeMillis() - getTimeNotificationAppears() > milliseconds6Hours
        if (should) setTimeNotificationAppears()
        return should
    }

    private fun getRemoteNotificationContent() =
        sharedPreferences.getInt(PREFERENCES_NOTIFICATION_CONTENT, Ads.notification_content)

    fun getNotificationContent(): String {
        return when (getRemoteNotificationContent()) {
            1 -> context.getString(R.string.msg_notification_1)
            2 -> context.getString(R.string.msg_notification_2)
            3 -> context.getString(R.string.msg_notification_3)
            4 -> context.getString(R.string.msg_notification_4)
            else -> context.getString(R.string.msg_notification_5)
        }
    }

}