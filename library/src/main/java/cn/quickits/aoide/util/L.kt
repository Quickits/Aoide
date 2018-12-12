package cn.quickits.aoide.util

import android.util.Log

object L {

    private const val TAG = "Aoide"

    var isDebug = false

    fun logi(msg: String) {
        if (isDebug) Log.i(TAG, msg)
    }

    fun loge(message: String, throwable: Throwable? = null) {
        if (isDebug) Log.e(TAG, message, throwable)
    }

    fun logd(message: String) {
        if (isDebug) Log.d(TAG, message)
    }

}