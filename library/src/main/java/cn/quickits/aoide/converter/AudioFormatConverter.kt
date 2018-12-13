package cn.quickits.aoide.converter

import cn.quickits.aoide.recorder.Recorder

abstract class AudioFormatConverter(
    internal val sampleRateInHz: Int,
    internal val channels: Int,
    internal val bitsPerSample: Int
) {

    internal var filePath: String? = null

    internal var isOpen = false

    open fun open(filePath: String): Boolean {
        this.filePath = filePath
        isOpen = false
        return false
    }

    open fun close(): Boolean {
        isOpen = false
        return true
    }

    abstract fun convert(recorder: Recorder)

    abstract fun fileExtensionName(): String

}