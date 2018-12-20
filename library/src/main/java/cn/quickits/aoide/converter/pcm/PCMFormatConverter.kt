package cn.quickits.aoide.converter.pcm

import cn.quickits.aoide.converter.AudioFormatConverter
import cn.quickits.aoide.recorder.AudioRecorder
import cn.quickits.aoide.recorder.Recorder


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-20 14:51
 **/
class PCMFormatConverter(sampleRateInHz: Int, channels: Int, bitsPerSample: Int) :
    AudioFormatConverter(sampleRateInHz, channels, bitsPerSample) {

    override fun fileExtensionName(): String = ".pcm"

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
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    companion object {
        fun create() = PCMFormatConverter(AudioRecorder.DEFAULT_SAMPLE_RATE, 1, 16)
    }

}