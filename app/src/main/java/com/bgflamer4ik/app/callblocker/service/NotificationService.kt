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

        val notification = createNotification(text)
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    private fun createNotification(text: String): Notification {
        val intent = Intent(this, Main::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0,
            intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(this.applicationContext.getString(R.string.app_name))
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "Call Blocker notifications"
        private const val NOTIFICATION_ID = 1
    }
}