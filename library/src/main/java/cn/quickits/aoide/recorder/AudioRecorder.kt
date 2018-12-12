package cn.quickits.aoide.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import cn.quickits.aoide.util.GlobalVars.isRecording
import cn.quickits.aoide.util.L


class AudioRecorder : IRecorder {

    var audioRecord: AudioRecord? = null

    var onRecordStateChangedListener: OnRecordStateChangedListener? = null

    private var minBufferSize = 0

    override fun startAudioRecord() {
        startAudioRecord(
            DEFAULT_SOURCE,
            DEFAULT_SAMPLE_RATE,
            DEFAULT_CHANNEL_CONFIG,
            DEFAULT_AUDIO_FORMAT
        )
    }

    private fun startAudioRecord(
        audioSource: Int,
        sampleRateInHz: Int,
        channelConfig: Int,
        audioFormat: Int
    ) {
        if (isRecording) return

        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRateInHz, channelConfig, audioFormat
        )

        if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            return
        }

        minBufferSize = bufferSize

        val audioRecord = AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize)

        if (audioRecord.state == AudioRecord.STATE_UNINITIALIZED) {
            return
        }

        this.audioRecord = audioRecord

        audioRecord.startRecording()

        onRecordStateChangedListener?.onStartRecord(audioRecord)

        isRecording = true
    }

    override fun stopAudioRecord() {
        if (!isRecording) return

        val audioRecord = this.audioRecord ?: return

        if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.stop()
        }

        audioRecord.release()

        isRecording = false
    }

    override fun readBuffer(): ByteArray? {
        val audioRecord = this.audioRecord ?: return null

        val buffer = ByteArray(minBufferSize)
        if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            val rect = audioRecord.read(buffer, 0, minBufferSize)

            when (rect) {
                AudioRecord.ERROR_INVALID_OPERATION -> {
                    L.logi("Recording <= ERROR_INVALID_OPERATION")
                }

                AudioRecord.ERROR_BAD_VALUE -> {
                    L.logi("Recording <= ERROR_BAD_VALUE")
                }

                else -> {
                    L.logi("Recording <= $rect")
                    return buffer
                }
            }
        }

        return null
    }

    interface OnRecordStateChangedListener {
        fun onStartRecord(audioRecord: AudioRecord)

        fun onStopRecord()
    }

    companion object {
        private const val DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC
        const val DEFAULT_SAMPLE_RATE = 44100
        private const val DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}