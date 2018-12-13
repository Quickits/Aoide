package cn.quickits.aoide

import android.content.Context
import cn.quickits.aoide.core.TaskCreatorFactory
import cn.quickits.aoide.core.Status
import cn.quickits.aoide.core.TaskBox
import io.reactivex.Flowable
import io.reactivex.Maybe

object Aoide {

    fun with(context: Context): TaskCreatorFactory = TaskCreatorFactory(context)

    fun start(): Maybe<Any>? = TaskBox.getInstance().getCurrentTask()?.start()

    fun stop(): Maybe<Any>? = TaskBox.getInstance().getCurrentTask()?.stop()

    fun pause(): Maybe<Any>? = TaskBox.getInstance().getCurrentTask()?.pause()

    fun status(): Flowable<Status>? = TaskBox.getInstance().getCurrentTask()?.getFlowable()
}