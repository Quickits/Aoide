package cn.quickits.aoide.sample.ui

import android.support.v7.util.AdapterListUpdateCallback
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import cn.quickits.aoide.sample.repo.bean.RecordFile
import cn.quickits.aoide.sample.util.diff.RecordFilesDiffCallback


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:28
 **/
class RecordFilesAdapter(private val listener: RecordFilesViewHolder.Listener) :
    RecyclerView.Adapter<RecordFilesViewHolder>() {

    var list: List<RecordFile>? = null
        set(value) {
            val diffResult = DiffUtil.calculateDiff(
                RecordFilesDiffCallback(
                    field,
                    value
                )
            )
            field = value
            diffResult.dispatchUpdatesTo(AdapterListUpdateCallback(this))
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordFilesViewHolder =
        RecordFilesViewHolder(parent, listener)

    override fun getItemCount(): Int = list?.size ?: 0

    override fun onBindViewHolder(holder: RecordFilesViewHolder, position: Int) {
        val recordFile = list?.get(position) ?: return
        holder.onBind(recordFile)
    }

}