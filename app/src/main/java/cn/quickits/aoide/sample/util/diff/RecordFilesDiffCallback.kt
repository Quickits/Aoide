package cn.quickits.aoide.sample.util.diff

import android.support.v7.util.DiffUtil
import cn.quickits.aoide.sample.repo.bean.RecordFile


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:06
 **/
class RecordFilesDiffCallback(
    private val oldData: List<RecordFile>?,
    private val newData: List<RecordFile>?
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldData?.size ?: 0

    override fun getNewListSize(): Int = newData?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldData?.get(oldItemPosition) == newData?.get(newItemPosition)

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFile = oldData?.get(oldItemPosition)
        val newFile = newData?.get(newItemPosition)

        return oldFile?.filePath?.equals(newFile?.filePath) == true
    }

}