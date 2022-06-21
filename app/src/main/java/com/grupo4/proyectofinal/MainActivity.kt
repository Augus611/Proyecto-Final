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
import android.view.WindowManager
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var canvasView : CanvasView
    lateinit var sensorManager : SensorManager
    lateinit var accelerometer : Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        canvasView = CanvasView(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE)
        setContentView(canvasView)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onResume() {
        super.onResume()
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                val x = event.x - canvasView.buttonLoginPosition[0]
                val y = event.y - canvasView.buttonLoginPosition[1]
                val distance = sqrt(x * x + y * y)
                if (distance <= canvasView.buttonLoginPosition[2]) {
                    startActivity(
                        Intent(this, Activity_Login::class.java)
                    )
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onAccuracyChanged(event: Sensor?, p1: Int) {
    }
}
