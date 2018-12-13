package cn.quickits.aoide.encoder

object AoideSoftEncoder {

    init {
        System.loadLibrary("aoide")
    }

    external fun mp3EncodeInit(inSamplerate: Int, inChannel: Int, outSamplerate: Int, outBitrate: Int, quality: Int)

    external fun mp3EncodeWrite(bufferLeft: ShortArray, bufferRight: ShortArray, count: Int, mp3buf: ByteArray): Int

    external fun mp3EncodeFlush(mp3buf: ByteArray): Int

    external fun mp3EncodeClose()

}
