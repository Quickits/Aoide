package cn.quickits.aoide.recorder

interface Recorder {

    fun startAudioRecord()

    fun stopAudioRecord()

    fun isRecording(): Boolean

    fun readBuffer(): PCMData?

    fun readShortBuffer(): PCMData?

    fun setOnRecordStateChangedListener(onRecordStateChangedListener: OnRecordStateChangedListener)

}