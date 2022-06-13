package com.grupo4.proyectofinal

import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kotlin.random.Random

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var canvasView : CanvasView
    lateinit var sensorManager : SensorManager
    lateinit var accelerometer : Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        canvasView = CanvasView(this, this)
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
                        canvasView.tempPosX = canvasView.posX - 5f
                    }
                    event.values[0] < -0.5 -> {
                        canvasView.tempPosX = canvasView.posX + 5f
                    }
                }
                val leftMargin =  canvasView.centerX - canvasView.sizeX + canvasView.spaceship.width / 2f
                val rightMargin = canvasView.centerX + canvasView.sizeX - canvasView.spaceship.width / 2f
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

class Asteroid (bmp: Bitmap){

    val posX = (80..1000).random(Random(System.nanoTime())).toFloat()
    var posY = -100f
    val bitmap = bmp

    fun move() {
        posY += 1f
    }

}

class Asteroids (private val asteroidBitmaps: List<Bitmap>){

    var list = mutableListOf<Asteroid>()

    fun addAsteroid() {
        val randomIndex = (asteroidBitmaps.indices).random()
        list.add(Asteroid(asteroidBitmaps[randomIndex]))
    }

    fun removeAsteroid(asteroid: Asteroid){
        list.remove(asteroid)
    }

    fun moveAsteroids() {
        for (asteroid in list) {
            asteroid.move()
        }
    }

}