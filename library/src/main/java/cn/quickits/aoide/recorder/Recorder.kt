package cn.quickits.aoide.recorder

interface Recorder {

    fun startAudioRecord()

    fun stopAudioRecord()

    fun readBuffer(): PCMData?

    fun readShortBuffer(): PCMData?

    fun setOnRecordStateChangedListener(onRecordStateChangedListener: OnRecordStateChangedListener)

}