package cn.quickits.aoide.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cn.quickits.aoide.Aoide
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start.setOnClickListener {
            Aoide.get(File(externalCacheDir, "123.aac").absolutePath).subscribe()
        }

        stop.setOnClickListener {
            Aoide.stop(File(externalCacheDir, "123.aac").absolutePath).subscribe()
        }

        pause.setOnClickListener {
            Aoide.pause(File(externalCacheDir, "123.aac").absolutePath).subscribe()
        }
    }
}
