package com.example.avjindersinghsekhon.minimaltodo.utility

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.util.Log
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.reminder.ReminderActivity

class TodoNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("OskarSchindler", "onReceiver called")
        intent?.let { intent ->
            val todoText = intent.getStringExtra(TODOTEXT)
            val todoUUID = intent.getUUIDExtra(TODOUUID)
            todoUUID?.let {
                val manager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val i = Intent(context, ReminderActivity::class.java)
                i.putExtra(TODOUUID, it)
                val deleteIntent = Intent(context, DeleteNotificationReceiver::class.java)
                deleteIntent.putExtra(TODOUUID, it)
                val channel = NotificationChannel(it.toString(), "notchannel", IMPORTANCE_LOW)
                val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
                manager.createNotificationChannel(channel)
                val notification = Notification.Builder(context, channel.id)
                        .setContentTitle(todoText)
                        .setSmallIcon(R.drawable.ic_done_white_24dp)
                        .setAutoCancel(true)
                        .setDeleteIntent(PendingIntent.getService(context, it.hashCode(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setContentIntent(PendingIntent.getActivity(context, it.hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT))
                        .build()
                manager.notify(100, notification)
            }
        }
    }

    companion object {
        const val TODOTEXT = "com.avjindersekhon.todonotificationservicetext"
        const val TODOUUID = "com.avjindersekhon.todonotificationserviceuuid"
    }
}
