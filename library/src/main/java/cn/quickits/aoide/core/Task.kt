package cn.quickits.aoide.core

import cn.quickits.aoide.converter.AudioFormatConverter
import cn.quickits.aoide.recorder.AudioRecorder
import cn.quickits.aoide.recorder.OnRecordStateChangedListener
import cn.quickits.aoide.recorder.Recorder
import cn.quickits.aoide.util.L
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import io.reactivex.schedulers.Schedulers
import java.io.File


class Task(taskSpec: TaskSpec) {

    private val recorder: Recorder = AudioRecorder()

    private var disposable: Disposable? = null

    private lateinit var statusFlowable: FlowableProcessor<Status>

    private lateinit var converter: AudioFormatConverter

    private lateinit var targetFile: String

    private lateinit var currentStatus: Status

    private var stopRecordFlag: Boolean = false
    private var recordedFlag: Boolean = false


    init {
        recorder.setOnRecordStateChangedListener(object : OnRecordStateChangedListener {
            override fun onStartRecord() {
                processorData(recorder)
            }

            override fun onStopRecord() {

            }
        })

        init(taskSpec)
    }

    internal fun init(taskSpec: TaskSpec) {
        if (::statusFlowable.isInitialized && !statusFlowable.hasComplete()) statusFlowable.onComplete()

        converter = taskSpec.converter

        targetFile = taskSpec.cachePath + File.separator + System.currentTimeMillis() + converter.fileExtensionName()

        statusFlowable = BehaviorProcessor.create<Status>().toSerialized()

        stopRecordFlag = false

        emitStatus(Prepared(targetFile))
    }

    internal fun start(): Maybe<Any> {
        return Maybe.create<Any> {
            doStartRecord()
            it.onSuccess(Any())
        }.subscribeOn(Schedulers.newThread())
    }

    internal fun stop(): Maybe<Any> {
        return Maybe.create<Any> {
            doStopRecord()
            it.onSuccess(Any())
        }.subscribeOn(Schedulers.newThread())
    }

    internal fun pause(): Maybe<Any> {
        return Maybe.create<Any> {
            doPauseRecord()
            it.onSuccess(Any())
        }.subscribeOn(Schedulers.newThread())
    }

    internal fun getStatus() = currentStatus

    internal fun getFlowable() = statusFlowable

    private fun doStartRecord() {
        if (!recorder.isRecording() && !stopRecordFlag) {
            stopRecordFlag = false
            recordedFlag = true
            if (!converter.isOpen) converter.open(targetFile)

            recorder.startAudioRecord()

            emitStatus(Recording(currentStatus))
        } else {

        }
    }

    private fun doStopRecord() {
        if (recorder.isRecording()) {
            stopRecordFlag = true
            recorder.stopAudioRecord()
        } else {
            if (recordedFlag) {
                emitStatus(Completed(currentStatus))
            }
        }
    }

    private fun doPauseRecord() {
        if (recorder.isRecording()) {
            recorder.stopAudioRecord()
            emitStatus(Paused(currentStatus))
        } else {
        }
    }

    private fun emitStatus(status: Status) {
        currentStatus = status
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
                emitStatus(Error(e, currentStatus))
            }, {
                if (stopRecordFlag) emitStatus(Completed(currentStatus))
            })
    }

    private fun converter(audioRecorder: Recorder): Recorder {
        converter.convert(audioRecorder)
        return audioRecorder
    }

    private fun closeConverter() {
        if (stopRecordFlag) {
            converter.close()
            recordedFlag = false
            L.logi("closeConverter by stop")
        } else if (currentStatus is Paused) {
            L.logi("closeConverter by pause")
        }
    }

    private fun isLoopConverter(processor: BehaviorProcessor<Recorder>, audioRecorder: Recorder) {
        val isRecording = recorder.isRecording()
        L.logi("isLoopConverter: $isRecording")

        if (isRecording) {
            processor.onNext(audioRecorder)
        } else {
            processor.onComplete()
        }
    }

}
