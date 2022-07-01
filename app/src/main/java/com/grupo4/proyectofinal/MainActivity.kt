package com.grupo4.proyectofinal

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var canvasView : CanvasView
    private lateinit var layout : RelativeLayout
    lateinit var sensorManager : SensorManager
    lateinit var accelerometer : Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        canvasView = CanvasView(this)
        layout = RelativeLayout(this)
        var params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        layout.addView(
            canvasView,
            params
        )
        val loginButton = ImageButton(this)
        loginButton.setImageResource(R.drawable.spaceship)
        loginButton.setOnClickListener{
            startActivity(
                Intent(this, Activity_Login::class.java)
            )
        }
        params = RelativeLayout.LayoutParams(100, 100)
        params.setMargins(30, windowManager.defaultDisplay.height/2 - canvasView.sizeY.toInt() - 100, 0, 0)
        layout.addView(
            loginButton,
            params
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setFullscreen()
        setContentView(layout)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onResume() {
        super.onResume()
        setFullscreen()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (canvasView.start) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                when {
                    event.values[0] > 0.5 -> {
                        canvasView.tempPosX = canvasView.spaceship.posX - canvasView.spaceship.speed
                    }
                    event.values[0] < -0.5 -> {
                        canvasView.tempPosX = canvasView.spaceship.posX + canvasView.spaceship.speed
                    }
                }
                val leftMargin =  canvasView.centerX - canvasView.sizeX + canvasView.spaceship.bitmap.width / 2f
                val rightMargin = canvasView.centerX + canvasView.sizeX - canvasView.spaceship.bitmap.width / 2f
                if (canvasView.tempPosX in leftMargin..rightMargin) {
                    canvasView.spaceship.posX = canvasView.tempPosX
                } else {
                    when {
                        canvasView.tempPosX <= leftMargin -> canvasView.spaceship.posX = leftMargin
                        canvasView.tempPosX >= rightMargin -> canvasView.spaceship.posX = rightMargin
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setFullscreen()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (canvasView.sizeChanged) {
            setFullscreen()
            canvasView.sizeChanged = false
        }
        return super.onTouchEvent(event)
    }

    fun setFullscreen() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    override fun onAccuracyChanged(event: Sensor?, p1: Int) {
    }
}
