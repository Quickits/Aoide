package cn.quickits.aoide.core

class TaskBox private constructor() {

    private val box = arrayListOf<Task>()

    fun create(taskSpec: TaskSpec): Task? {
        val current = getCurrentTask()

        if (current?.isPaused == true) {
            return current
        } else if (current != null) {
            return null
        }

        val task = Task(taskSpec)

        box.add(task)

        return task
    }

    fun getCurrentTask(): Task? {
        for (task in box) {
            if (!task.isFinished) return task
        }
        return null
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