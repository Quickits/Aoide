package cn.quickits.aoide.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cn.quickits.aoide.Aoide
import cn.quickits.aoide.core.*
import cn.quickits.aoide.util.L
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        L.isDebug = true

        disposable = Aoide.with(this).mp3()
            .create()
            .subscribe { status ->
                when (status) {
                    is Prepared -> {
                        println("Prepared")
                    }

                    is Recording -> {
                        println("Recording")
                    }

                    is Paused -> {
                        println("Paused")
                    }

                    is Completed -> {
                        println("Completed: " + status.filePath)
                    }

                    is Error -> {
                        println("Error")
                        status.throwable.printStackTrace()
                    }
                }
            }

        start.setOnClickListener {
            Aoide.start()?.subscribe()
        }

        stop.setOnClickListener {
            Aoide.stop()?.subscribe()
        }

        pause.setOnClickListener {
            Aoide.pause()?.subscribe()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
