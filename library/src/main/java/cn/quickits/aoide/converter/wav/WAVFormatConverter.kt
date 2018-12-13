package cn.quickits.aoide.converter.wav

import cn.quickits.aoide.converter.AudioFormatConverter
import cn.quickits.aoide.recorder.AudioRecorder
import cn.quickits.aoide.recorder.Recorder
import cn.quickits.aoide.util.L
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WAVFormatConverter(sampleRateInHz: Int, channels: Int, bitsPerSample: Int) : AudioFormatConverter(
    sampleRateInHz,
    channels,
    bitsPerSample
) {

    private var dataSize = 0

    private var wavHeader: WAVHeader? = null

    private var randomAccessFile: RandomAccessFile? = null

    override fun fileExtensionName(): String = ".wav"

    override fun open(filePath: String): Boolean {
        super.open(filePath)

        val file = File(filePath)

        if (file.exists() && readHeader()) {

        } else {
            file.delete()
            writeHeader(sampleRateInHz, channels, bitsPerSample)
        }

        dataSize = wavHeader!!.mSubChunk2Size

        randomAccessFile = RandomAccessFile(file, "rw")

        return true
    }

    override fun close(): Boolean {
        super.close()

        var ret = true
        if (randomAccessFile != null) {
            ret = writeDataSize()
            randomAccessFile!!.close()
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

    private fun writeHeader(sampleRateInHz: Int, channels: Int, bitsPerSample: Int): Boolean {
        val dataOutputStream = DataOutputStream(FileOutputStream(filePath))

        val header = WAVHeader(sampleRateInHz, channels, bitsPerSample)

        try {
            dataOutputStream.writeBytes(header.mChunkID)
            dataOutputStream.write(intToByteArray(header.mChunkSize), 0, 4)
            dataOutputStream.writeBytes(header.mFormat)
            dataOutputStream.writeBytes(header.mSubChunk1ID)
            dataOutputStream.write(intToByteArray(header.mSubChunk1Size), 0, 4)
            dataOutputStream.write(shortToByteArray(header.mAudioFormat), 0, 2)
            dataOutputStream.write(shortToByteArray(header.mNumChannel), 0, 2)
            dataOutputStream.write(intToByteArray(header.mSampleRate), 0, 4)
            dataOutputStream.write(intToByteArray(header.mByteRate), 0, 4)
            dataOutputStream.write(shortToByteArray(header.mBlockAlign), 0, 2)
            dataOutputStream.write(shortToByteArray(header.mBitsPerSample), 0, 2)
            dataOutputStream.writeBytes(header.mSubChunk2ID)
            dataOutputStream.write(intToByteArray(header.mSubChunk2Size), 0, 4)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            dataOutputStream.close()
        }

        wavHeader = header

        return true
    }

    private fun readHeader(): Boolean {
        val dataInputStream = DataInputStream(FileInputStream(filePath))

        val header = WAVHeader()

        val intValue = ByteArray(4)
        val shortValue = ByteArray(2)

        try {
            header.mChunkID = "" + dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar() +
                    dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar()
            L.logi("Read file chunkID:" + header.mChunkID)

            dataInputStream.read(intValue)
            header.mChunkSize = byteArrayToInt(intValue)
            L.logi("Read file chunkSize:" + header.mChunkSize)

            header.mFormat = "" + dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar() +
                    dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar()
            L.logi("Read file format:" + header.mFormat)

            header.mSubChunk1ID = "" + dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar() +
                    dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar()
            L.logi("Read fmt chunkID:" + header.mSubChunk1ID)

            dataInputStream.read(intValue)
            header.mSubChunk1Size = byteArrayToInt(intValue)
            L.logi("Read fmt chunkSize:" + header.mSubChunk1Size)

            dataInputStream.read(shortValue)
            header.mAudioFormat = byteArrayToShort(shortValue)
            L.logi("Read audioFormat:" + header.mAudioFormat)

            dataInputStream.read(shortValue)
            header.mNumChannel = byteArrayToShort(shortValue)
            L.logi("Read channel number:" + header.mNumChannel)

            dataInputStream.read(intValue)
            header.mSampleRate = byteArrayToInt(intValue)
            L.logi("Read samplerate:" + header.mSampleRate)

            dataInputStream.read(intValue)
            header.mByteRate = byteArrayToInt(intValue)
            L.logi("Read byterate:" + header.mByteRate)

            dataInputStream.read(shortValue)
            header.mBlockAlign = byteArrayToShort(shortValue)
            L.logi("Read blockalign:" + header.mBlockAlign)

            dataInputStream.read(shortValue)
            header.mBitsPerSample = byteArrayToShort(shortValue)
            L.logi("Read bitspersample:" + header.mBitsPerSample)

            header.mSubChunk2ID = "" + dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar() +
                    dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar()
            L.logi("Read data chunkID:" + header.mSubChunk2ID)

            dataInputStream.read(intValue)
            header.mSubChunk2Size = byteArrayToInt(intValue)
            L.logi("Read data chunkSize:" + header.mSubChunk2Size)

            L.logi("Read wav file success !")
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            dataInputStream.close()
        }

        wavHeader = header

        return true
    }

    private fun writeDataSize(): Boolean {
        val wavFile = randomAccessFile ?: return false

        try {
            wavFile.seek(WAVHeader.WAV_CHUNK_SIZE_OFFSET.toLong())
            wavFile.write(intToByteArray(dataSize + WAVHeader.WAV_CHUNK_SIZE_EXCLUDE_DATA), 0, 4)
            wavFile.seek(WAVHeader.WAV_SUB_CHUNK_SIZE2_OFFSET.toLong())
            wavFile.write(intToByteArray(dataSize), 0, 4)
            wavFile.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun intToByteArray(data: Int): ByteArray {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array()
    }

    private fun shortToByteArray(data: Short): ByteArray {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array()
    }

    private fun byteArrayToShort(b: ByteArray): Short {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).short
    }

    private fun byteArrayToInt(b: ByteArray): Int {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).int
    }

    companion object {
        fun create() = WAVFormatConverter(AudioRecorder.DEFAULT_SAMPLE_RATE, 1, 16)
    }

}
