package cn.quickits.aoide.encoder

interface IEncoder {

    fun openFile(filePath: String, sampleRateInHz: Int, channels: Int, bitsPerSample: Int): Boolean

    fun closeFile(): Boolean

    fun writeData(buffer: ByteArray, offset: Int, count: Int): Boolean

}