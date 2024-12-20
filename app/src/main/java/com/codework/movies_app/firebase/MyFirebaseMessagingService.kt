package com.codework.movies_app.firebase

import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.codework.movies_app.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.remoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService(){

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message from: ${remoteMessage.from}")
        val title = remoteMessage.notification?.title ?: "Notification"
        val body = remoteMessage.notification?.body ?: "You have a new message"
        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification?.title ?: "Notification"
            val body = remoteMessage.notification?.body ?: "You have a new message"
            showNotification(title, body)
        } else if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Notification"
            val body = remoteMessage.data["body"] ?: "You have a new message"
            showNotification(title, body)
        }
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New token: $token")
    }
    private fun showNotification(title: String, body: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.circle_active)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                val notificationManager = NotificationManagerCompat.from(this)
                notificationManager.notify(0, builder.build())

            } else {
                Log.e(TAG, "POST_NOTIFICTIONS permission not granted. Cannot show notifications.")
            }
        } else {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.circle_active)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(0, builder.build())
        }


    }
    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "channel_id"
    }
}