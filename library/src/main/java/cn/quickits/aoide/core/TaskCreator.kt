package cn.quickits.aoide.core

import android.content.Context
import cn.quickits.aoide.converter.AudioFormatConverter
import io.reactivex.Flowable

class TaskCreator(context: Context, encoder: AudioFormatConverter) {

    private val taskSpec = TaskSpec.cleanInstance(context)

    init {
        taskSpec.converter = encoder
    }

    fun create(): Flowable<Status> {
        return TaskBox.getInstance().create(taskSpec)
    }

}