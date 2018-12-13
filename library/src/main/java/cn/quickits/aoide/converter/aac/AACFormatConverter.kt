package cn.quickits.aoide.converter.aac

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import cn.quickits.aoide.converter.AudioFormatConverter
import cn.quickits.aoide.recorder.AudioRecorder
import cn.quickits.aoide.recorder.Recorder
import cn.quickits.aoide.util.L
import java.io.File
import java.io.RandomAccessFile

@Suppress("DEPRECATION")
class AACFormatConverter(sampleRateInHz: Int, channels: Int, bitsPerSample: Int) : AudioFormatConverter(
    sampleRateInHz,
    channels,
    bitsPerSample
) {

    private var mediaCodec: MediaCodec? = null

    private var bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()

    private val kSampleRates = intArrayOf(8000, 11025, 22050, 44100, 48000)

    private val kBitRates = intArrayOf(64000, 96000, 128000)

    override fun fileExtensionName(): String = ".aac"

    override fun open(filePath: String): Boolean {
        super.open(filePath)

        mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE_AUDIO_AAC)

        val mediaFormat = MediaFormat.createAudioFormat(MIME_TYPE_AUDIO_AAC, sampleRateInHz, channels)
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, kBitRates[1])
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024 * 1024)

        mediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        mediaCodec?.start()
        return true
    }

    override fun close(): Boolean {
        super.close()
        mediaCodec?.stop()
        mediaCodec?.release()
        randomAccessFile?.close()
        return true
    }

    override fun convert(recorder: Recorder) {
        val pcmData = recorder.readBuffer() ?: return
        if (pcmData.buffer?.size ?: 0 > 0) {
            writeData(pcmData.buffer!!, 0, pcmData.count)
        }
    }

    private fun writeData(buffer: ByteArray, offset: Int, count: Int): Boolean {
        val mediaCodec = this.mediaCodec ?: return false

        val inputBufferIndex = mediaCodec.dequeueInputBuffer(-1)

        L.logi("AAC => input: $inputBufferIndex")

        if (inputBufferIndex >= 0) {
            val inputBuffer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaCodec.getInputBuffer(inputBufferIndex)
            } else {
                mediaCodec.inputBuffers[inputBufferIndex]
            }

            inputBuffer?.clear()
            inputBuffer?.put(buffer)
            inputBuffer?.limit(count)
            mediaCodec.queueInputBuffer(inputBufferIndex, offset, count, 0, 0)
        }

        var outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0)
        while (outputBufferIndex >= 0) {

            L.logi("AAC => output: $outputBufferIndex")

            val packetSize = bufferInfo.size + 7

            val outputBuffer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaCodec.getOutputBuffer(outputBufferIndex)
            } else {
                mediaCodec.outputBuffers[outputBufferIndex]
            }

            outputBuffer?.position(bufferInfo.offset)
            outputBuffer?.limit(bufferInfo.offset + bufferInfo.size)

            val byteArray = ByteArray(packetSize)
            AACHeader.writeADTSHeader(byteArray, packetSize)
            outputBuffer?.get(byteArray, 7, bufferInfo.size)
            outputBuffer?.position(bufferInfo.offset)

            randomAccessFile?.seek(randomAccessFile?.length() ?: 0)
            randomAccessFile?.write(byteArray)

            mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0)
        }

        return true
    }

    companion object {
        private const val MIME_TYPE_AUDIO_AAC = "audio/mp4a-latm"
        fun create() = AACFormatConverter(AudioRecorder.DEFAULT_SAMPLE_RATE, 1, 16)
    }

}