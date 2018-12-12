package cn.quickits.aoide.core

import android.content.Context
import cn.quickits.aoide.encoder.IAudioFileEncoder
import cn.quickits.aoide.encoder.wav.WAVEncoder

class TaskSpec {

    internal var cachePath: String = ""

    internal var fileEncoder: IAudioFileEncoder = WAVEncoder()

    private fun reset(context: Context) {
        fileEncoder = WAVEncoder()
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
