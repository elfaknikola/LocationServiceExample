package com.example.locationserviceexample.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.locationserviceexample.R

class LocationNotificationHelper(
    private val context: Context
) {
    private val notificationManager =
        context.getSystemService(NotificationManager::class.java)

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Shows current location updates"
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Location service running")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    fun updateNotification(latitude: Double, longitude: Double) {
        val text = "Lat: $latitude, Lng: $longitude"

        notificationManager.notify(
            NOTIFICATION_ID,
            buildNotification(text)
        )
    }

    companion object {
        const val CHANNEL_ID = "location_channel"
        const val NOTIFICATION_ID = 1001

        private const val CHANNEL_NAME = "Location updates"
    }
}