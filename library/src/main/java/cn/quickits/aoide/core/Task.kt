package cn.quickits.aoide.core

import cn.quickits.aoide.recorder.AudioRecorder
import cn.quickits.aoide.recorder.Recorder
import cn.quickits.aoide.recorder.OnRecordStateChangedListener
import cn.quickits.aoide.util.GlobalVars
import cn.quickits.aoide.util.L
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import java.io.File


class Task(taskSpec: TaskSpec) {

    private val recorder: Recorder = AudioRecorder()

    private val statusFlowable = BehaviorProcessor.create<Status>().toSerialized()

    private var disposable: Disposable? = null

    private var converter = taskSpec.converter

    private var targetFile: String =
        taskSpec.cachePath + File.separator + System.currentTimeMillis() + converter.fileExtensionName()

    internal var isFinished: Boolean = false

    internal var isPaused: Boolean = false

    init {
        recorder.setOnRecordStateChangedListener(object : OnRecordStateChangedListener {
            override fun onStartRecord() {
                processorData(recorder)
            }

            override fun onStopRecord() {

            }
        })

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

            if (!converter.isOpen) converter.open(targetFile)

            recorder.startAudioRecord()

            emitStatus(Recording())
        }
    }

    private fun doStopRecord() {
        if (GlobalVars.isRecording) {
            isFinished = true
            recorder.stopAudioRecord()
        }
    }

    private fun doPauseRecord() {
        if (GlobalVars.isRecording) {
            isPaused = true
            recorder.stopAudioRecord()

            emitStatus(Paused())
        }
    }

    private fun emitStatus(status: Status) {
        statusFlowable.onNext(status)
    }

    private fun processorData(recorder: Recorder) {
        if (disposable != null && disposable?.isDisposed == false) return

        val processor = BehaviorProcessor.createDefault(recorder)

        disposable = processor
            .map { converter(it) }
            .doOnNext { isLoopConverter(processor, it) }
            .doOnComplete { closeConverter() }
            .doOnCancel { L.logi("processorData onCancel") }
            .doOnError { L.loge("processorData onError", it) }
            .doFinally { L.logi("doFinally") }
            .subscribeOn(Schedulers.io())
            .subscribe({
            }, { e ->
                emitStatus(Error(e))
            }, {
                if (isFinished) emitStatus(Completed(targetFile))
            })
    }

    private fun converter(audioRecorder: Recorder): Recorder {
        converter.convert(audioRecorder)
        return audioRecorder
    }

    private fun closeConverter() {
        if (isFinished) {
            converter.close()
            L.logi("closeConverter by stop")
        } else if (isPaused) {
            L.logi("closeConverter by pause")
        }
    }

    private fun isLoopConverter(processor: BehaviorProcessor<Recorder>, audioRecorder: Recorder) {
        val isRecording = GlobalVars.isRecording
        L.logi("isLoopConverter: $isRecording")

        if (isRecording) {
            processor.onNext(audioRecorder)
        } else {
            processor.onComplete()
        }
    }

}