package com.example.AproManager.kotlinCode.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.AproManager.R
import com.example.AproManager.javaCode.Activities.SignInActivity
import com.example.AproManager.kotlinCode.activities.MainActivity
import com.example.AproManager.kotlinCode.firebase.FirestoreClass
import com.example.AproManager.kotlinCode.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

       /* Log.d("NotificationHere", "onMessageReceived: Message received")
        Log.d(TAG, "Received FCM message: $message")
        Log.d(TAG, "From: ${message.from}")*/

        // Process data payload
        if (message.data.isNotEmpty()) {
            //Log.d(TAG, "Data payload: ${message.data}")
            val title = message.data[Constants.KEY_TITLE] ?: ""
            val messageFcm = message.data[Constants.KEY_MESSAGE] ?: ""
            sendNotification(title, messageFcm)
        } else {
           // Log.d(TAG, "Notification payload: ${message.notification}")
        }


    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //Log.e(TAG, "Refresh token: $token")
    }

    private fun sendNotification(title: String, message: String) {
        val intent = Intent(applicationContext,
            if (FirestoreClass().getCurrentUserID().isNotEmpty())
                MainActivity::class.java
            else
            SignInActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //  notification channel if Android version is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.default_notification_channel_description)
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // notification ID
        val notificationId = Random.nextInt(Int.MAX_VALUE)
        notificationManager.notify(notificationId, notificationBuilder.build())

        //Log.d(TAG, "Notification sent successfully with ID: $notificationId")
    }

    companion object {
        private const val TAG = "FirebaseMessageService"
    }
}
