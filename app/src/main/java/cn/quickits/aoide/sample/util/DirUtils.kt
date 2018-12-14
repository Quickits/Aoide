package cn.quickits.aoide.sample.util

import com.blankj.utilcode.util.Utils


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:05
 **/
object DirUtils {

    val cacheDirPath: String = Utils.getApp().externalCacheDir!!.absolutePath

}