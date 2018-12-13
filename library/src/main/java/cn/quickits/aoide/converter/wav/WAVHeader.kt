package cn.quickits.aoide.converter.wav

import cn.quickits.aoide.util.L
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.cert.CertPath

/**
 *
 * see more http://soundfile.sapp.org/doc/WaveFormat/
 *
 */
class WAVHeader {

    var mChunkID = "RIFF"
    var mChunkSize = 0
    var mFormat = "WAVE"

    var mSubChunk1ID = "fmt "
    var mSubChunk1Size = 16
    var mAudioFormat: Short = 1
    var mNumChannel: Short = 1
    var mSampleRate = 8000
    var mByteRate = 0
    var mBlockAlign: Short = 0
    var mBitsPerSample: Short = 8

    var mSubChunk2ID = "data"
    var mSubChunk2Size = 0

    constructor()

    constructor(sampleRateInHz: Int, channels: Int, bitsPerSample: Int) {
        mSampleRate = sampleRateInHz
        mBitsPerSample = bitsPerSample.toShort()
        mNumChannel = channels.toShort()
        mByteRate = mSampleRate * mNumChannel.toInt() * mBitsPerSample.toInt() / 8
        mBlockAlign = (mNumChannel * mBitsPerSample / 8).toShort()
    }

    companion object {
        const val WAV_FILE_HEADER_SIZE = 44
        const val WAV_CHUNK_SIZE_EXCLUDE_DATA = 36

        const val WAV_CHUNK_SIZE_OFFSET = 4
        const val WAV_SUB_CHUNK_SIZE1_OFFSET = 16
        const val WAV_SUB_CHUNK_SIZE2_OFFSET = 40

        fun writeHeader(filePath: String, sampleRateInHz: Int, channels: Int, bitsPerSample: Int): WAVHeader? {
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
                return null
            } finally {
                dataOutputStream.close()
            }

            return header
        }

        fun writeDataSize(randomAccessFile: RandomAccessFile?, dataSize: Int): Boolean {
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

        private fun readHeader(filePath: String): WAVHeader? {
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
                return null
            } finally {
                dataInputStream.close()
            }

            return header
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
    }

}
