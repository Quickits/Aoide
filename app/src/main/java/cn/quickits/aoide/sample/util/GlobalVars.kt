package cn.quickits.aoide.sample.util

import android.os.SystemClock

object GlobalVars {

    const val FORMAT_MP3 = 1
    const val FORMAT_AAC = 2
    const val FORMAT_WAV = 3

    var recordingFormat: Int = FORMAT_MP3

    var isRecording = false

    var startRecordingTime: Long = 0

    var currentRecordingFile: String? = null

    fun startRecording(file: String) {
        currentRecordingFile = file
        isRecording = true
        startRecordingTime = SystemClock.elapsedRealtime()

        RxBus.get().post(RxBus.OnRecordingStatusChangeEvent())
    }

    fun stopRecording() {
        currentRecordingFile = null
        isRecording = false
        startRecordingTime = 0

        RxBus.get().post(RxBus.OnRecordingStatusChangeEvent())
    }
}