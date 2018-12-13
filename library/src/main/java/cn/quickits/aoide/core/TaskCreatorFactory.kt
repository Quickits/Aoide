package cn.quickits.aoide.core

import android.content.Context
import cn.quickits.aoide.converter.aac.AACFormatConverter
import cn.quickits.aoide.converter.mp3.Mp3FormatConverter
import cn.quickits.aoide.converter.wav.WAVFormatConverter

class TaskCreatorFactory(val context: Context) {

    fun aac() = TaskCreator(context, AACFormatConverter.create())

    fun mp3() = TaskCreator(context, Mp3FormatConverter.create())

    fun wav() = TaskCreator(context, WAVFormatConverter.create())

}