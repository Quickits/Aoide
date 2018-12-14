package cn.quickits.aoide.sample.util

import com.hwangjr.rxbus.Bus

/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:03
 */
object RxBus {

    private var sBus: Bus? = null

    @Synchronized
    fun get(): Bus {
        if (sBus == null) {
            sBus = Bus()
        }
        return sBus as Bus
    }

    class OnRecordingStatusChangeEvent

}
