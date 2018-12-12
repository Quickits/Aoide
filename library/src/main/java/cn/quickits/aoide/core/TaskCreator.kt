package cn.quickits.aoide.core

import android.content.Context
import cn.quickits.aoide.util.AHRTException
import cn.quickits.aoide.encoder.aac.AACEncoder
import cn.quickits.aoide.encoder.wav.WAVEncoder
import io.reactivex.Flowable
import java.lang.IllegalArgumentException

class TaskCreator(context: Context) {

    private val taskSpec = TaskSpec.cleanInstance(context)

    fun fileEncoder(encoderType: String): TaskCreator {
        taskSpec.fileEncoder = when (encoderType) {
            TYPE_FILE_ENCODER_AAC -> AACEncoder()
            TYPE_FILE_ENCODER_WAV -> WAVEncoder()
            else -> throw IllegalArgumentException("encoder type not find")
        }
        return this
    }

    fun create(): Flowable<Status> {
        val task = TaskBox.getInstance().create(taskSpec) ?: return Flowable.just(Error(AHRTException()))
        return task.getFlowable()
    }

    companion object {
        const val TYPE_FILE_ENCODER_AAC = "type_file_encoder_aac"
        const val TYPE_FILE_ENCODER_WAV = "type_file_encoder_wav"
    }
}