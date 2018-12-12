package cn.quickits.aoide.core

open class Status

class Prepared : Status()

class Recording : Status()

class Paused : Status()

class Completed(val filePath: String) : Status()

class Error(val throwable: Throwable) : Status()
