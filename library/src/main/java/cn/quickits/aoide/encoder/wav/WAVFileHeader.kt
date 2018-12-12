package cn.quickits.aoide.encoder.wav

/**
 *
 * see more http://soundfile.sapp.org/doc/WaveFormat/
 *
 */
class WAVFileHeader {

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
    }

}
