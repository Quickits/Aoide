package cn.quickits.aoide.core

import io.reactivex.Flowable

class TaskBox private constructor() {

    private lateinit var task: Task

    fun create(taskSpec: TaskSpec): Flowable<Status> {
        if (::task.isInitialized) {
            if (task.getStatus() is Prepared || task.getStatus() is Completed) {
                task.init(taskSpec)
            }
        } else {
            task = Task(taskSpec)
        }

        return task.getFlowable()
    }

    fun getCurrentTask(): Task? {
        return if (::task.isInitialized && task.getStatus() !is Completed) {
            task
        } else {
            null
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: TaskBox? = null

        fun getInstance(): TaskBox {
            return INSTANCE ?: synchronized(TaskBox::class) {
                INSTANCE ?: TaskBox().also { INSTANCE = it }
            }
        }

    }

}