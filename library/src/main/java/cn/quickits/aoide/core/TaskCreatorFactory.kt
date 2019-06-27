package cn.quickits.aoide.core

import android.content.Context
import cn.quickits.aoide.converter.aac.AACFormatConverter
import cn.quickits.aoide.converter.mp3.Mp3FormatConverter
import cn.quickits.aoide.converter.pcm.PCMFormatConverter
import cn.quickits.aoide.converter.wav.WAVFormatConverter
import cn.quickits.aoide.recorder.AudioRecorder

class TaskCreatorFactory(internal val context: Context) {

    fun aac() = TaskCreator(context, AACFormatConverter.create())

    fun mp3(sampleRateInHz: Int = AudioRecorder.DEFAULT_SAMPLE_RATE, channels: Int = 1, bitsPerSample: Int = 16) =
        TaskCreator(context, Mp3FormatConverter.create())

    fun wav() = TaskCreator(context, WAVFormatConverter.create())

    fun pcm() = TaskCreator(context, PCMFormatConverter.create())

}