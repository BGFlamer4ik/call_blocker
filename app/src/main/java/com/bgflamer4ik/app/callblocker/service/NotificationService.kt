package com.bgflamer4ik.app.callblocker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bgflamer4ik.app.callblocker.Main
import com.bgflamer4ik.app.callblocker.R

object NotificationKeys {
    const val EMPTY_KEY = "empty"
    const val BLOCK_KEY = "block"
    const val PROGRESS = "progress"
    const val DATA_EXPORT = "export"
}

class NotificationService: Service() {
    override fun onBind(p0: Intent?): IBinder? = null
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            CHANNEL_ID,
            this.applicationContext.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_MIN
        )

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, p0: Int, p1: Int): Int {
        val text = intent?.getStringExtra("text")
            ?: this.applicationContext.getString(R.string.notification_test)
        val key = intent?.getStringExtra("key") ?: NotificationKeys.EMPTY_KEY
        val fileUri = intent?.getStringExtra("fileUri")

        var intent: Intent
        var pendingIntent: PendingIntent
        var notification: Notification

        when(key) {
            NotificationKeys.DATA_EXPORT -> {
                if (fileUri.isNullOrEmpty()) {
                    notification = createNotification("Error, file uri is empty!")
                } else {
                    intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    pendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        Intent.createChooser(intent, this.getString(R.string.share)),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    notification = createNotification(text, pendingIntent)
                }
            }

            NotificationKeys.BLOCK_KEY -> {
                intent = Intent(this, Main::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                pendingIntent = PendingIntent.getActivity(
                    this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                notification = createNotification(text, pendingIntent)
            }

            NotificationKeys.PROGRESS -> {
                notification = createNotificationProgress(text)
            }

            else -> notification = createNotification(text)
        }

        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    private fun createNotification(text: String, pendingIntent: PendingIntent): Notification {
        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(this.applicationContext.getString(R.string.app_name))
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    private fun createNotification(text: String): Notification {
        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(this.applicationContext.getString(R.string.app_name))
            .setContentText(text)
            .setAutoCancel(true)
            .build()
    }

    private fun createNotificationProgress(text: String): Notification {
        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(this.applicationContext.getString(R.string.app_name))
            .setContentText(text)
            .setProgress(100, 0, true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "Call Blocker notifications"
        private const val NOTIFICATION_ID = 1
    }
}