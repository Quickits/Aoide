package cn.quickits.aoide.sample.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.quickits.aoide.sample.R
import cn.quickits.aoide.sample.repo.bean.RecordFile
import kotlinx.android.synthetic.main.item_record_file.view.*


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:28
 **/
class RecordFilesViewHolder(parent: ViewGroup, private val listener: Listener) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_record_file,
        parent,
        false
    )
) {

    fun onBind(recordFile: RecordFile) {
        itemView.tv_file_name.text = recordFile.fileName

        itemView.setOnClickListener {
            listener.onPlayClick(recordFile)
        }

        itemView.btn_delete.setOnClickListener {
            listener.onDeleteClick(recordFile)
        }
    }

    interface Listener {
        fun onPlayClick(recordFile: RecordFile)

        fun onDeleteClick(recordFile: RecordFile)
    }

}