package com.example.slide.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.slide.R
import com.example.slide.notify.NotificationConfig.CHANNEL_ID
import com.example.slide.notify.NotificationConfig.NOTIFICATION_ID
import com.example.slide.ui.home.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceive : BroadcastReceiver() {

    @Inject
    lateinit var notificationSetter: NotificationSetter

    private val notificationRepository by lazy {
        notificationSetter.notificationRepository
    }

    private fun startNotification(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, createBuilder(context).build())
        }
    }

    private fun createPendingIntent(context: Context): PendingIntent? {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }
        return PendingIntent.getActivity(context, 0, notificationIntent, flags)
    }

    private fun createBuilder(context: Context): NotificationCompat.Builder {
        val contentView = RemoteViews(context.packageName, R.layout.notification_remind).apply {
            setTextViewText(R.id.tv_contents, notificationRepository.getNotificationContent())
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_center)
            .setCustomContentView(contentView)
            .setCustomBigContentView(contentView)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createPendingIntent(context))
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification),
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(CHANNEL_ID)
        }
        return builder
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (notificationRepository.shouldReceive()) {
            startNotification(context)
            notificationSetter.schedule(context)
        }
    }

}