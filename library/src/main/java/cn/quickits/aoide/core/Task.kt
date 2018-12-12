package cn.quickits.aoide.core

import android.media.AudioRecord
import cn.quickits.aoide.recorder.AudioRecorder
import cn.quickits.aoide.util.GlobalVars
import cn.quickits.aoide.util.GlobalVars.isRecording
import cn.quickits.aoide.util.L
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import java.io.File

class Task(taskSpec: TaskSpec) {

    private val audioRecorder: AudioRecorder = AudioRecorder()

    private val statusFlowable = BehaviorProcessor.create<Status>().toSerialized()

    private var disposable: Disposable? = null

    internal var isFinished: Boolean = false

    internal var isPaused: Boolean = false

    private var fileEncoder = taskSpec.fileEncoder

    private var targetFile: String =
        taskSpec.cachePath + File.separator + System.currentTimeMillis() + fileEncoder.fileExtensionName()

    init {
        audioRecorder.onRecordStateChangedListener = object : AudioRecorder.OnRecordStateChangedListener {
            override fun onStartRecord(audioRecord: AudioRecord) {
                processorData(audioRecorder)
            }

            override fun onStopRecord() {
                if (disposable?.isDisposed == false) disposable?.dispose()
                disposable = null
            }
        }

        emitStatus(Prepared())
    }

    fun start(): Maybe<Any> {
        return Maybe.create<Any> {
            doStartRecord()
            it.onSuccess(Any())
        }.subscribeOn(Schedulers.newThread())
    }

    fun stop(): Maybe<Any> {
        return Maybe.create<Any> {
            doStopRecord()
            it.onSuccess(Any())
        }.subscribeOn(Schedulers.newThread())
    }

    fun pause(): Maybe<Any> {
        return Maybe.create<Any> {
            doPauseRecord()
            it.onSuccess(Any())
        }.subscribeOn(Schedulers.newThread())
    }

    fun getFlowable() = statusFlowable

    private fun doStartRecord() {
        if (!GlobalVars.isRecording && !isFinished) {
            isPaused = false
            isFinished = false
            fileEncoder.openFile(targetFile, AudioRecorder.DEFAULT_SAMPLE_RATE, 1, 16)
            audioRecorder.startAudioRecord()

            emitStatus(Recording())
        }
    }

    private fun doStopRecord() {
        if (GlobalVars.isRecording) {
            isFinished = true
            audioRecorder.stopAudioRecord()
        }
    }

    private fun doPauseRecord() {
        if (GlobalVars.isRecording) {
            isPaused = true
            audioRecorder.stopAudioRecord()

            emitStatus(Paused())
        }
    }

    private fun emitStatus(status: Status) {
        statusFlowable.onNext(status)
    }

    private fun processorData(audioRecorder: AudioRecorder) {
        if (disposable != null && disposable?.isDisposed == false) return

        val subject = BehaviorProcessor.createDefault(audioRecorder)

        disposable = subject
            .map {
                val buffer = audioRecorder.readBuffer()

                if (buffer?.size ?: 0 > 0) {
                    fileEncoder.writeData(buffer!!, 0, buffer.size)
                }

                it
            }
            .doOnNext {
                if (isRecording) {
                    subject.onNext(it)
                } else {
                    subject.onComplete()
                }
            }
            .doOnError { L.loge("processorData onError", it) }
            .doOnComplete { fileEncoder.closeFile() }
            .doOnCancel { L.logi("processorData onCancel") }
            .doFinally { L.logi("doFinally") }
            .subscribeOn(Schedulers.io())
            .subscribe({
            }, { e ->
                emitStatus(Error(e))
            }, {
                if (isFinished) emitStatus(Completed(targetFile))
            })
    }

}