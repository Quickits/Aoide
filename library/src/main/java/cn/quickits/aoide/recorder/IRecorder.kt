package cn.quickits.aoide.recorder

interface IRecorder {

    fun startAudioRecord()

    fun stopAudioRecord()

    fun readBuffer(): ByteArray?

}