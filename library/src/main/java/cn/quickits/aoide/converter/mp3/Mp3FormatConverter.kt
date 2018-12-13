package cn.quickits.aoide.converter.mp3

import cn.quickits.aoide.encoder.AoideSoftEncoder
import cn.quickits.aoide.converter.AudioFormatConverter
import cn.quickits.aoide.recorder.AudioRecorder
import cn.quickits.aoide.recorder.Recorder
import java.io.File
import java.io.RandomAccessFile

class Mp3FormatConverter(sampleRateInHz: Int, channels: Int, bitsPerSample: Int) : AudioFormatConverter(
    sampleRateInHz,
    channels,
    bitsPerSample
) {

    private var randomAccessFile: RandomAccessFile? = null

    private var mp3Buffer: ByteArray? = null

    override fun fileExtensionName(): String = ".mp3"

    override fun open(filePath: String): Boolean {
        super.open(filePath)
        val file = File(filePath)
        if (!file.exists()) {
            file.createNewFile()
        }
        randomAccessFile = RandomAccessFile(file, "rw")

        AoideSoftEncoder.mp3EncodeInit(sampleRateInHz, channels, sampleRateInHz, 32, 7)
        return true
    }

    override fun close(): Boolean {
        super.close()
        val encodedSize = AoideSoftEncoder.mp3EncodeFlush(mp3Buffer)

        if (encodedSize > 0) {
            randomAccessFile?.seek(randomAccessFile?.length() ?: 0)
            randomAccessFile?.write(mp3Buffer, 0, encodedSize)
        }

        randomAccessFile?.close()

        AoideSoftEncoder.mp3EncodeClose()
        return true
    }

    override fun convert(recorder: Recorder) {
        val pcmData = recorder.readShortBuffer() ?: return

        if (pcmData.shortBuffer?.size ?: 0 > 0) {
            writeData(pcmData.shortBuffer!!, pcmData.count)
        }
    }

    private fun writeData(shortBuffer: ShortArray, count: Int): Boolean {
        val mp3Buffer = this.mp3Buffer ?: createMp3Buffer(shortBuffer.size)

        val encodedSize = AoideSoftEncoder.mp3EncodeWrite(shortBuffer, shortBuffer, count, mp3Buffer)

        if (encodedSize > 0) {
            randomAccessFile?.seek(randomAccessFile?.length() ?: 0)
            randomAccessFile?.write(mp3Buffer, 0, encodedSize)
        }

        return true
    }

    private fun createMp3Buffer(size: Int): ByteArray {
        val buffer = ByteArray((7200 + (size * 2 * 1.25)).toInt())
        mp3Buffer = buffer
        return buffer
    }

    companion object {
        fun create() = Mp3FormatConverter(AudioRecorder.DEFAULT_SAMPLE_RATE, 1, 16)
    }

}