package cn.quickits.aoide.core

class TaskBox private constructor() {

    private val box = arrayListOf<Task>()

    fun get(filePath: String): Task {
        for (task in box) {
            if (task.targetFile == filePath) return task
        }

        val task = Task(filePath)

        box.add(task)

        return task
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