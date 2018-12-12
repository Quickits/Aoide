package cn.quickits.aoide.encoder.wav

import android.content.ContentValues.TAG
import android.util.Log
import cn.quickits.aoide.encoder.IEncoder
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WAVEncoder : IEncoder {

    private var mFilepath: String? = null
    private var mDataSize = 0
    private var wavFileHeader: WAVFileHeader? = null

    private var randomAccessFile: RandomAccessFile? = null

    override fun openFile(filePath: String, sampleRateInHz: Int, channels: Int, bitsPerSample: Int): Boolean {
        mFilepath = filePath

        val file = File(filePath)

        if (file.exists() && readHeader()) {

        } else {
            file.delete()
            writeHeader(sampleRateInHz, channels, bitsPerSample)
        }

        mDataSize = wavFileHeader!!.mSubChunk2Size

        randomAccessFile = RandomAccessFile(mFilepath, "rw")

        return true
    }

    override fun closeFile(): Boolean {
        var ret = true
        if (randomAccessFile != null) {
            ret = writeDataSize()
            randomAccessFile!!.close()
            randomAccessFile = null
        }
        return ret
    }

    override fun writeData(buffer: ByteArray, offset: Int, count: Int): Boolean {
        if (randomAccessFile == null) {
            return false
        }

        try {
            randomAccessFile!!.seek(randomAccessFile!!.length())
            randomAccessFile!!.write(buffer, offset, count)
            mDataSize += count
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    private fun writeHeader(sampleRateInHz: Int, channels: Int, bitsPerSample: Int): Boolean {
        val dataOutputStream = DataOutputStream(FileOutputStream(mFilepath))

        val header = WAVFileHeader(sampleRateInHz, channels, bitsPerSample)

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

        wavFileHeader = header

        return true
    }

    private fun readHeader(): Boolean {
        val dataInputStream = DataInputStream(FileInputStream(mFilepath))

        val header = WAVFileHeader()

        val intValue = ByteArray(4)
        val shortValue = ByteArray(2)

        try {
            header.mChunkID = "" + dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar() +
                    dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar()
            Log.d(TAG, "Read file chunkID:" + header.mChunkID)

            dataInputStream.read(intValue)
            header.mChunkSize = byteArrayToInt(intValue)
            Log.d(TAG, "Read file chunkSize:" + header.mChunkSize)

            header.mFormat = "" + dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar() +
                    dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar()
            Log.d(TAG, "Read file format:" + header.mFormat)

            header.mSubChunk1ID = "" + dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar() +
                    dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar()
            Log.d(TAG, "Read fmt chunkID:" + header.mSubChunk1ID)

            dataInputStream.read(intValue)
            header.mSubChunk1Size = byteArrayToInt(intValue)
            Log.d(TAG, "Read fmt chunkSize:" + header.mSubChunk1Size)

            dataInputStream.read(shortValue)
            header.mAudioFormat = byteArrayToShort(shortValue)
            Log.d(TAG, "Read audioFormat:" + header.mAudioFormat)

            dataInputStream.read(shortValue)
            header.mNumChannel = byteArrayToShort(shortValue)
            Log.d(TAG, "Read channel number:" + header.mNumChannel)

            dataInputStream.read(intValue)
            header.mSampleRate = byteArrayToInt(intValue)
            Log.d(TAG, "Read samplerate:" + header.mSampleRate)

            dataInputStream.read(intValue)
            header.mByteRate = byteArrayToInt(intValue)
            Log.d(TAG, "Read byterate:" + header.mByteRate)

            dataInputStream.read(shortValue)
            header.mBlockAlign = byteArrayToShort(shortValue)
            Log.d(TAG, "Read blockalign:" + header.mBlockAlign)

            dataInputStream.read(shortValue)
            header.mBitsPerSample = byteArrayToShort(shortValue)
            Log.d(TAG, "Read bitspersample:" + header.mBitsPerSample)

            header.mSubChunk2ID = "" + dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar() +
                    dataInputStream.readByte().toChar() + dataInputStream.readByte().toChar()
            Log.d(TAG, "Read data chunkID:" + header.mSubChunk2ID)

            dataInputStream.read(intValue)
            header.mSubChunk2Size = byteArrayToInt(intValue)
            Log.d(TAG, "Read data chunkSize:" + header.mSubChunk2Size)

            Log.d(TAG, "Read wav file success !")
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            dataInputStream.close()
        }

        wavFileHeader = header

        return true
    }

    private fun writeDataSize(): Boolean {
        val wavFile = randomAccessFile ?: return false

        try {
            wavFile.seek(WAVFileHeader.WAV_CHUNK_SIZE_OFFSET.toLong())
            wavFile.write(intToByteArray(mDataSize + WAVFileHeader.WAV_CHUNK_SIZE_EXCLUDE_DATA), 0, 4)
            wavFile.seek(WAVFileHeader.WAV_SUB_CHUNK_SIZE2_OFFSET.toLong())
            wavFile.write(intToByteArray(mDataSize), 0, 4)
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
}
