package com.dac.gapp.andac.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.dac.gapp.andac.MainActivity
import com.dac.gapp.andac.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber


class FcmReciveService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        val message = remoteMessage?:return
        Timber.d(message.from)

        // Check if message contains a data payload.
        if (message.data.isNotEmpty()) {
            Timber.d("Message data payload: " + message.data)
            showNotification(message)
        }
    }

    fun showNotification(message: RemoteMessage) {

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val mBuilder = NotificationCompat.Builder(this, "notice")
                .setSmallIcon(R.drawable.andac_logo)
                .setContentTitle(message.data.get("title"))
                .setContentText(message.data.get("content"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "notice"
            val description = "notice"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notice", name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationsystemManager = getSystemService(NotificationManager::class.java)
            notificationsystemManager!!.createNotificationChannel(channel)
        }

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, mBuilder.build())
    }


}