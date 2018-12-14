package cn.quickits.aoide.sample.widget

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatSeekBar
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import cn.quickits.aoide.sample.R
import cn.quickits.aoide.sample.repo.bean.RecordFile
import cn.quickits.aoide.sample.util.TimeUtils


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:07
 **/
class PlayerDialog(context: Context) {

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val dialog: AlertDialog = AlertDialog.Builder(context).create()

    private var seekBar: AppCompatSeekBar?

    private var playTime: TextView?

    private var mediaPlayer: MediaPlayer

    private var recordFile: RecordFile? = null

    init {
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_player, null, false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setView(v)
        dialog.setContentView(R.layout.dialog_player)
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "关闭") { _, _ ->
            dismiss()
        }
        dialog.setOnShowListener {
            play()
        }
        dialog.setOnDismissListener {
            stop()
        }

        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnPreparedListener {
            if (!mediaPlayer.isPlaying) mediaPlayer.start()
            startUpdateUI()
        }
        mediaPlayer.setOnSeekCompleteListener {
            if (!mediaPlayer.isPlaying) mediaPlayer.start()
            startUpdateUI()
        }
        mediaPlayer.setOnCompletionListener {
            stopUpdateUI()
        }

        playTime = v.findViewById(R.id.tv_play_time)
        seekBar = v.findViewById(R.id.seek_bar)
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer.seekTo(seekBar?.progress ?: 0)
            }
        })
    }

    fun show(recordFile: RecordFile) {
        this.recordFile = recordFile

        if (!dialog.isShowing) {
            dialog.setTitle(recordFile.fileName)
            dialog.show()
        }
    }

    fun release() {
        stop()
        mediaPlayer.release()
    }

    private fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    private fun play() {
        val file = recordFile ?: return
        mediaPlayer.reset()
        mediaPlayer.setDataSource(file.filePath)
        mediaPlayer.prepare()
    }

    private fun stop() {
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer.reset()

        stopUpdateUI()
    }

    private fun startUpdateUI() {
        handler.post(updateSeekBarRunnable)
    }

    private fun stopUpdateUI() {
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    private fun updateUI() {
        seekBar?.max = mediaPlayer.duration
        seekBar?.progress = mediaPlayer.currentPosition

        playTime?.text = TimeUtils.converTime(mediaPlayer.currentPosition)

        handler.postDelayed(updateSeekBarRunnable, 16)
    }

    private val updateSeekBarRunnable: Runnable = Runnable {
        updateUI()
    }

}