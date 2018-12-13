package cn.quickits.aoide.converter

import cn.quickits.aoide.recorder.Recorder
import java.io.File
import java.io.RandomAccessFile

abstract class AudioFormatConverter(
    internal val sampleRateInHz: Int,
    internal val channels: Int,
    internal val bitsPerSample: Int
) {

    private var filePath: String? = null

    internal var randomAccessFile: RandomAccessFile? = null

    internal var isOpen = false

    open fun open(filePath: String): Boolean {
        if (isOpen) throw RuntimeException("Converter is already opened.")

        this.filePath = filePath

        val file = File(filePath)

        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        } else if (file.exists()) {
            file.delete()
        }

        file.createNewFile()

        randomAccessFile = RandomAccessFile(file, "rw")

        isOpen = true

        return false
    }

    open fun close(): Boolean {
        isOpen = false
        return true
    }

    abstract fun convert(recorder: Recorder)

    abstract fun fileExtensionName(): String

}