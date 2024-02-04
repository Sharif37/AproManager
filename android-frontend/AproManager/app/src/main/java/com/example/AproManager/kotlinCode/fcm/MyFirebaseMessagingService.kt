package com.example.AproManager.kotlinCode.fcm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.AproManager.R
import com.example.AproManager.javaCode.Activities.SignInActivity
import com.example.AproManager.kotlinCode.activities.MainActivity
import com.example.AproManager.kotlinCode.firebase.FirestoreClass
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.remoteMessage
import com.example.AproManager.kotlinCode.utils.Constants

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG,"From: ${message.from}")
        message.data.isNotEmpty().let {
            Log.d(TAG,"message data payload: ${message.data}")
            val title= message.data[Constants.KEY_TITLE]!!
            val mgs=message.data[Constants.KEY_MESSAGE]!!
            sendNotification(title,mgs)

        }
        message.notification?.let {
            Log.d(TAG,"message Notification body: ${it.body}")

        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG,"Refresh token :$token ")

    }

    private fun sendNotification(title:String,message: String){
        val intent=if(FirestoreClass().getCurrentUserID().isNotEmpty()) {
            Intent(this,MainActivity::class.java)
        }else
        {
            Intent(this,SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP )
        val pendingIntent=PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val channelId=this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val notificationBuilder=NotificationCompat.Builder(
            this,channelId
        ).setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0,notificationBuilder.build())


    }
    private fun sendRegistrationToServer(token:String){

    }
    companion object{
        private const val TAG="FirebaseMessageService"
    }
}