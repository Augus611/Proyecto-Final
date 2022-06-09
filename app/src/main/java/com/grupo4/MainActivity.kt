package com.grupo4.proyectofinal

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var canvasView : CanvasView
    lateinit var sensorManager : SensorManager
    lateinit var accelerometer : Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        canvasView = CanvasView(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
                        canvasView.tempPosX = canvasView.posX - 5f
                    }
                    event.values[0] < -0.5 -> {
                        canvasView.tempPosX = canvasView.posX + 5f
                    }
                }
                val leftMargin =  canvasView.centerX - canvasView.sizeX + 50f
                val rightMargin = canvasView.width - canvasView.centerX + canvasView.sizeX - 50f
                if (canvasView.tempPosX in leftMargin..rightMargin) {
                    canvasView.posX = canvasView.tempPosX
                    canvasView.invalidate()
                } else {
                    when {
                        canvasView.tempPosX <= leftMargin -> canvasView.posX = leftMargin
                        canvasView.tempPosX >= rightMargin -> canvasView.posX = rightMargin
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(event: Sensor?, p1: Int) {
    }

}