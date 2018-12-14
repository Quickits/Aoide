package cn.quickits.aoide.sample.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import cn.quickits.aoide.sample.R
import cn.quickits.aoide.sample.ui.MainActivity
import com.blankj.utilcode.util.Utils


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:09
 **/
object NotificationBuilder {

    const val NOTIFICATION_ID = 1000

    private const val CHANNEL_ID = "default"

    private val notificationManager: NotificationManager by lazy {
        Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW)
            notificationChannel.enableLights(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun getServiceNotification(): Notification {
        createNotificationChannel()

        val intent = Intent(Utils.getApp(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(Utils.getApp(), 0, intent, 0)

        return NotificationCompat.Builder(Utils.getApp(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mic_black_24dp)
            .setContentTitle("Recording")
            .setContentIntent(pendingIntent)
            .setShowWhen(false)
            .build()
    }

}