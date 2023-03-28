package com.example.slide.notify

class NotificationRepository constructor(
    private val notificationShared: NotificationShared
) {

    fun setNotificationTime(alarmHour: Int, alarmMinute: Int) =
        notificationShared.setNotificationTime(alarmHour, alarmMinute)

    fun setRemoteNotificationContent(content: Int) =
        notificationShared.setRemoteNotificationContent(content)

    fun getAlarmHour(): Int = notificationShared.getAlarmHour()

    fun getAlarmMinute(): Int = notificationShared.getAlarmMinute()

    fun shouldCreateNewNotification(): Boolean =
        notificationShared.shouldCreateNotify()

    fun shouldReceive() = notificationShared.shouldReceive()

    fun getNotificationContent(): String = notificationShared.getNotificationContent()

}