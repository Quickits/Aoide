package cn.quickits.aoide.sample.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:06
 **/
object TimeUtils {

    @SuppressLint("SimpleDateFormat")
    fun converTime(long: Int): String = SimpleDateFormat("mm:ss").format(long)

}