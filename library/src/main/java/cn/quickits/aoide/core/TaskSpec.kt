package cn.quickits.aoide.core

import android.content.Context
import cn.quickits.aoide.converter.AudioFormatConverter
import cn.quickits.aoide.converter.wav.WAVFormatConverter


class TaskSpec {

    internal var cachePath: String = ""

    internal var converter: AudioFormatConverter = WAVFormatConverter.create()

    private fun reset(context: Context) {
        converter = WAVFormatConverter.create()
        cachePath = context.externalCacheDir?.absolutePath ?: context.cacheDir.absolutePath
    }

    private object InstanceHolder {
        internal val INSTANCE = TaskSpec()
    }

    companion object {

        private val INSTANCE: TaskSpec
            get() = InstanceHolder.INSTANCE

        fun cleanInstance(context: Context): TaskSpec {
            val taskSpec = INSTANCE
            taskSpec.reset(context)
            return taskSpec
        }
    }
}
