package cn.quickits.aoide

import cn.quickits.aoide.core.Status
import cn.quickits.aoide.core.TaskBox
import io.reactivex.Flowable
import io.reactivex.Maybe

object Aoide {

    fun get(filePath: String, autoStart: Boolean = true): Flowable<Status> {
        val flowable = TaskBox.getInstance().get(filePath).getFlowable()

        if (autoStart) {
            start(filePath).subscribe()
        }

        return flowable
    }

    fun start(filePath: String): Maybe<Any> {
        return TaskBox.getInstance().get(filePath).start()
    }

    fun stop(filePath: String): Maybe<Any> {
        return TaskBox.getInstance().get(filePath).stop()
    }

    fun pause(filePath: String): Maybe<Any> {
        return TaskBox.getInstance().get(filePath).pause()
    }

}