package com.github.picture2pc.android.data

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.picture2pc.android.MainActivity
import com.github.picture2pc.common.ui.NotificationHandler

class TrayNotificationHandler(private val context: Context) : NotificationHandler {
    override fun displayNotification(title: String, message: String, popup: Boolean) {
        if (popup) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            return
        }

        context.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel("1", "Notifications", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Channel for Notifications"
            }
        )

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(1, NotificationCompat.Builder(context, "1")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Picture2PC")
                .setContentText("Picture sent!")
                .setContentIntent(PendingIntent.getActivity(
                    context, 0, Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }, PendingIntent.FLAG_IMMUTABLE
                ))
                .setAutoCancel(true)
                .build())
        } else {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }
}

