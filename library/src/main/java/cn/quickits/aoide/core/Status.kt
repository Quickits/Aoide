package cn.quickits.aoide.core

open class Status {

    var filePath: String

    constructor(filePath: String) {
        this.filePath = filePath
    }

    constructor(status: Status) {
        filePath = status.filePath
    }

}

class Prepared(filePath: String) : Status(filePath)

class Recording(status: Status) : Status(status)

class Paused(status: Status) : Status(status)

class Completed(status: Status) : Status(status)

class Error(val throwable: Throwable, status: Status) : Status(status)
