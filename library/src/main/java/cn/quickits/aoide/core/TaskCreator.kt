package cn.quickits.aoide.core

import android.content.Context
import cn.quickits.aoide.converter.AudioFormatConverter
import cn.quickits.aoide.util.AHRTException
import io.reactivex.Flowable

class TaskCreator(context: Context, encoder: AudioFormatConverter) {

    private val taskSpec = TaskSpec.cleanInstance(context)

    init {
        taskSpec.converter = encoder
    }

    fun create(): Flowable<Status> {
        val task = TaskBox.getInstance().create(taskSpec) ?: return Flowable.just(Error(AHRTException()))
        return task.getFlowable()
    }

}