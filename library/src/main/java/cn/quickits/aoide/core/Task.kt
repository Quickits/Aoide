package cn.quickits.aoide.core

import android.media.AudioRecord
import cn.quickits.aoide.encoder.wav.WavEncoder
import cn.quickits.aoide.recorder.AudioRecorder
import cn.quickits.aoide.util.GlobalVars
import cn.quickits.aoide.util.GlobalVars.isRecording
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers

class Task(var targetFile: String) {

    private val audioRecorder: AudioRecorder = AudioRecorder()

    private val statusFlowable = BehaviorProcessor.create<Status>().toSerialized()

    private var disposable: Disposable? = null

    private var isFinished: Boolean = false

    private var wavEncoder = WavEncoder()

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
            wavEncoder.openFile(targetFile, AudioRecorder.DEFAULT_SAMPLE_RATE, 2, 16)
            audioRecorder.startAudioRecord()
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
            audioRecorder.stopAudioRecord()
        }
    }

    private fun processorData(audioRecorder: AudioRecorder) {
        if (disposable != null && disposable?.isDisposed == false) return

        val subject = BehaviorProcessor.createDefault(audioRecorder)

        disposable = subject
            .map {
                val buffer = audioRecorder.readBuffer()

                if (buffer?.size ?: 0 > 0) {
                    wavEncoder.writeData(buffer!!, 0, buffer.size)
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
            .doOnError {
                println("doOnError")
            }
            .doOnComplete {
                wavEncoder.closeFile()
                println("doOnComplete")
            }
            .doOnCancel {
                println("doOnCancel")
            }
            .doFinally {
                println("doFinally")
            }
            .subscribeOn(Schedulers.io())
            .subscribe({

            }, {
                it.printStackTrace()
            }, {
                println("onComplete")
            })
    }

    override fun hashCode(): Int {
        return targetFile.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

}