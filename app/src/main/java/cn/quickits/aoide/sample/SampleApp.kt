package cn.quickits.aoide.sample

import android.app.Application
import com.blankj.utilcode.util.Utils


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 11:18
 **/
class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }

}