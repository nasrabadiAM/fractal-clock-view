package com.nasrabadiam.fractalclcok

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.fractalClock
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler().apply {
            postDelayed(object : Runnable {
                override fun run() {
                    updateClock()
                    handler?.postDelayed(this, 50)
                }
            }, 50)
        }
    }

    fun updateClock() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val milliSeconds = calendar.get(Calendar.MILLISECOND)
        fractalClock.updateTime(hour, minute, second, milliSeconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler = null
    }
}