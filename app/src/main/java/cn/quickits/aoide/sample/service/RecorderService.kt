package cn.quickits.aoide.sample.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import cn.quickits.aoide.Aoide
import cn.quickits.aoide.core.*
import cn.quickits.aoide.sample.util.GlobalVars
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:10
 **/
class RecorderService : Service() {

    private lateinit var disposable: Disposable

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecording()
        return START_STICKY
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }

    private fun startRecording() {
        val aoide = when (GlobalVars.recordingFormat) {
            GlobalVars.FORMAT_AAC -> Aoide.with(this).aac().create()
            GlobalVars.FORMAT_WAV -> Aoide.with(this).wav().create()
            else -> Aoide.with(this).mp3().create()
        }

        disposable = aoide.observeOn(AndroidSchedulers.mainThread())
            .subscribe { status ->
                when (status) {
                    is Prepared -> {
                        Aoide.start()?.subscribe()
                    }

                    is Recording -> {
                        GlobalVars.startRecording(status.filePath)
                        startForeground(
                            NotificationBuilder.NOTIFICATION_ID,
                            NotificationBuilder.getServiceNotification()
                        )
                    }

                    is Paused -> {
                        println("Paused")
                    }

                    is Completed -> {
                        println("Completed: " + status.filePath)
                        GlobalVars.stopRecording()
                        stopSelf()
                    }

                    is Error -> {
                        println("Error")
                        status.throwable.printStackTrace()
                        GlobalVars.stopRecording()
                    }
                }
            }

    }

    private fun stopRecording() {
        Aoide.stop()?.subscribe()
    }

}