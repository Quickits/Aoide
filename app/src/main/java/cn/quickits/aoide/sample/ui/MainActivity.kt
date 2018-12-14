package cn.quickits.aoide.sample.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.quickits.aoide.sample.R
import cn.quickits.aoide.util.L


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        L.isDebug = true

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragment.instance())
                .commit()
        }
    }

}
