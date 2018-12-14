package cn.quickits.aoide.sample.ui

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RadioGroup
import cn.quickits.aoide.sample.R
import cn.quickits.aoide.sample.repo.bean.RecordFile
import cn.quickits.aoide.sample.service.RecorderService
import cn.quickits.aoide.sample.util.GlobalVars
import cn.quickits.aoide.sample.util.RxBus
import cn.quickits.aoide.sample.widget.PlayerDialog
import cn.quickits.arch.mvvm.QLceViewFragment
import cn.quickits.arch.mvvm.data.ErrorData
import com.blankj.utilcode.util.ToastUtils
import com.hwangjr.rxbus.annotation.Subscribe
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:29
 **/
class MainFragment : QLceViewFragment<List<RecordFile>, RecordFilesViewModel, RecyclerView>() {

    private lateinit var adapter: RecordFilesAdapter

    private lateinit var playerDialog: PlayerDialog

    override fun pageName(): String = "MainPage"

    override fun bindLayout(): Int = R.layout.fragment_main

    override fun createViewModel(): RecordFilesViewModel =
        ViewModelProviders.of(this).get(RecordFilesViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBus.get().register(this)
        playerDialog = PlayerDialog(activity!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unregister(this)
        playerDialog.release()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        initTime()

        initBtnStatus()

        setupBtnClick()
    }

    private fun initAdapter() {
        adapter = RecordFilesAdapter(object : RecordFilesViewHolder.Listener {
            override fun onPlayClick(recordFile: RecordFile) {
                if (GlobalVars.isRecording) {
                    ToastUtils.showShort("正在录音...")
                } else {
                    playerDialog.show(recordFile)
                }
            }

            override fun onDeleteClick(recordFile: RecordFile) {
                viewModel.delete(recordFile)
            }
        })

        contentView.adapter = adapter
    }

    private fun initTime() {
        if (GlobalVars.isRecording) {
            tv_time.base = GlobalVars.startRecordingTime
            tv_time.start()
        } else {
            tv_time.base = SystemClock.elapsedRealtime()
            tv_time.stop()
        }
    }


    private fun setupBtnClick() {
        btn_record.setOnClickListener {
            RxPermissions(this)
                .requestEach(Manifest.permission.RECORD_AUDIO)
                .subscribe({ p ->
                    if (p.granted) {
                        val intent = Intent(context, RecorderService::class.java)
                        context?.startService(intent)
                        btn_record.isEnabled = false
                        btn_record_finish.isEnabled = true

                        disableRadioGroup(radio_group)
                    }
                }, { })
        }

        btn_record_finish.setOnClickListener {
            val intent = Intent(context, RecorderService::class.java)
            context?.stopService(intent)
            btn_record.isEnabled = true
            btn_record_finish.isEnabled = false

            enableRadioGroup(radio_group)
        }

        radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_aac -> GlobalVars.recordingFormat = GlobalVars.FORMAT_AAC
                R.id.radio_mp3 -> GlobalVars.recordingFormat = GlobalVars.FORMAT_MP3
                R.id.radio_wav -> GlobalVars.recordingFormat = GlobalVars.FORMAT_WAV
            }
        }
    }

    private fun initBtnStatus() {
        if (GlobalVars.isRecording) {
            btn_record.isEnabled = false
            btn_record_finish.isEnabled = true

            disableRadioGroup(radio_group)
        } else {
            btn_record.isEnabled = true
            btn_record_finish.isEnabled = false

            enableRadioGroup(radio_group)
        }

        radio_mp3.isChecked = GlobalVars.recordingFormat == GlobalVars.FORMAT_MP3
        radio_aac.isChecked = GlobalVars.recordingFormat == GlobalVars.FORMAT_AAC
        radio_wav.isChecked = GlobalVars.recordingFormat == GlobalVars.FORMAT_WAV
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.load(false)
    }

    override fun showContent(content: List<RecordFile>?) {
        super.showContent(content)
        adapter.list = content
    }

    override fun showError(errorData: ErrorData?) {
        showContent(null)
        super.showError(errorData)
    }

    @Subscribe()
    fun onRecordingStatusChangeEvent(event: RxBus.OnRecordingStatusChangeEvent) {
        initTime()
        if (!GlobalVars.isRecording) {
            viewModel.load(true)
        }
    }

    private fun disableRadioGroup(testRadioGroup: RadioGroup) {
        for (i in 0 until testRadioGroup.childCount) {
            testRadioGroup.getChildAt(i).isEnabled = false
        }
    }

    private fun enableRadioGroup(testRadioGroup: RadioGroup) {
        for (i in 0 until testRadioGroup.childCount) {
            testRadioGroup.getChildAt(i).isEnabled = true
        }
    }

    companion object {
        fun instance() = MainFragment()
    }

}