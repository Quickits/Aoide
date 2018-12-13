package cn.quickits.aoide.converter.wav

import cn.quickits.aoide.converter.AudioFormatConverter
import cn.quickits.aoide.recorder.AudioRecorder
import cn.quickits.aoide.recorder.Recorder


class WAVFormatConverter(sampleRateInHz: Int, channels: Int, bitsPerSample: Int) : AudioFormatConverter(
    sampleRateInHz,
    channels,
    bitsPerSample
) {

    private var dataSize = 0

    private var wavHeader: WAVHeader? = null

    override fun fileExtensionName(): String = ".wav"

    override fun open(filePath: String): Boolean {
        super.open(filePath)

        wavHeader = WAVHeader.writeHeader(filePath, sampleRateInHz, channels, bitsPerSample)

        dataSize = wavHeader!!.mSubChunk2Size
        return true
    }

    override fun close(): Boolean {
        super.close()

        var ret = true
        if (randomAccessFile != null) {
            ret = WAVHeader.writeDataSize(randomAccessFile, dataSize)
            randomAccessFile?.close()
            randomAccessFile = null
        }
        return ret
    }

    override fun convert(recorder: Recorder) {
        val pcmData = recorder.readBuffer() ?: return
        if (pcmData.buffer?.size ?: 0 > 0) {
            writeData(pcmData.buffer!!, 0, pcmData.count)
        }
    }

    private fun writeData(buffer: ByteArray, offset: Int, count: Int): Boolean {
        if (randomAccessFile == null) {
            return false
        }

        try {
            randomAccessFile!!.seek(randomAccessFile!!.length())
            randomAccessFile!!.write(buffer, offset, count)
            dataSize += count
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    companion object {
        fun create() = WAVFormatConverter(AudioRecorder.DEFAULT_SAMPLE_RATE, 1, 16)
    }

}
