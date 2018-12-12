package cn.quickits.aoide.encoder.aac

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import cn.quickits.aoide.encoder.IEncoder
import java.io.File
import java.io.FileOutputStream

class AACEncoder : IEncoder {

    private var mediaCodec: MediaCodec? = null
    private var bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()

    private var fileOutputStream: FileOutputStream? = null

    override fun openFile(filePath: String, sampleRateInHz: Int, channels: Int, bitsPerSample: Int): Boolean {
        val file = File(filePath)
        if (!file.exists()) {
            file.createNewFile()
        }
        fileOutputStream = FileOutputStream(file)

        mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE_AUDIO_AAC)

        val mediaFormat = MediaFormat.createAudioFormat(MIME_TYPE_AUDIO_AAC, sampleRateInHz, channels)
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000)
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024 * 1024)

        mediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        mediaCodec?.start()
        return true
    }

    override fun closeFile(): Boolean {
        mediaCodec?.stop()
        mediaCodec?.release()
        fileOutputStream?.flush()
        fileOutputStream?.close()
        return true
    }

    override fun writeData(buffer: ByteArray, offset: Int, count: Int): Boolean {
        val mediaCodec = this.mediaCodec ?: return false

        val inputBufferIndex = mediaCodec.dequeueInputBuffer(-1)
        println("ACC => input: $inputBufferIndex")
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
            println("ACC => output: $outputBufferIndex")

            val packetSize = bufferInfo.size + 7

            val outputBuffer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaCodec.getOutputBuffer(outputBufferIndex)
            } else {
                mediaCodec.outputBuffers[outputBufferIndex]
            }

            outputBuffer?.position(bufferInfo.offset)
            outputBuffer?.limit(bufferInfo.offset + bufferInfo.size)

            val byteArray = ByteArray(packetSize)
            addADTStoPacket(byteArray, packetSize)
            outputBuffer?.get(byteArray, 7, bufferInfo.size)
            outputBuffer?.position(bufferInfo.offset)

            fileOutputStream?.write(byteArray)

            mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0)
        }

        fileOutputStream?.flush()

        return true
    }

    private fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
        val profile = 2  //AAC LC
        val freqIdx = 4  //44.1KHz
        val chanCfg = 2  //CPE
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF9.toByte()
        packet[2] = ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7FF shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
    }

    companion object {
        private const val MIME_TYPE_AUDIO_AAC = "audio/mp4a-latm"
    }

}