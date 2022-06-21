package com.grupo4.proyectofinal

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Space
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import kotlin.math.sqrt
import kotlin.random.Random

class CanvasView(context: Context) : View(context){

    private val colorWhite = ResourcesCompat.getColor(resources, R.color.white, null)
    private val paintWhite = Paint().apply {
        color = colorWhite
        isAntiAlias = true
        isDither = true
    }
    private val colorBlack = ResourcesCompat.getColor(resources, R.color.black, null)
    private val paintBlack = Paint().apply {
        color = colorBlack
        style = Paint.Style.FILL
    }
    private val paint = Paint()
    var centerX = 0f
    var centerY = 0f
    var sizeX = 500f
    var sizeY = 800f
    var posX = 0f
    var tempPosX = 0f
    var posY = 0f
    var start = false
    var firstStart = true
    var spaceshipBitmap : Bitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(resources, R.drawable.spaceship,null),
        128,
        128,
        true
    )
    var asteroidBitmaps = listOf<Bitmap>(
        Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.asteroid,null),
            128,
            128,
            true
        )
    )
    var asteroids = Asteroids(asteroidBitmaps)
    var asteroidDistance = 100
    var spaceship = Spaceship(spaceshipBitmap)
    var buttonLoginPosition = arrayOf(0f, 0f, 32f)

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPaint(paintBlack)
        centerX = width / 2f
        centerY = height / 2f
        spaceship.posY = centerY + sizeY - 100
        if (firstStart) {
            spaceship.posX = centerX
            tempPosX = centerX
        }
        buttonLoginPosition[0] = centerX - sizeX + 40
        buttonLoginPosition[1] = centerY - sizeY - 50
        try {
            for (asteroid in asteroids.list) {
                canvas?.drawBitmap(
                    asteroid.bitmap,
                    asteroid.posX - asteroid.bitmap.width / 2,
                    asteroid.posY - asteroid.bitmap.height / 2,
                    paint
                )
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
        canvas?.drawCircle( //cambiar por un boton
            buttonLoginPosition[0],
            buttonLoginPosition[1],
            buttonLoginPosition[2],
            paintWhite
        )
        canvas?.drawRect(
            centerX - sizeX,
            centerY - sizeY,
            centerX + sizeX,
            centerY + sizeY,
            paintWhite.apply {
                style = Paint.Style.STROKE
                strokeWidth = 5f
            }
        )
        canvas?.drawBitmap(
            spaceship.bitmap,
            spaceship.posX - spaceship.bitmap.width / 2,
            spaceship.posY - spaceship.bitmap.height / 2,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (firstStart) {
                    createGameThread()
                    firstStart = false
                }
                start = !start
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun createGameThread() {
        val thread = Thread(Runnable {
            val fps : Long = 60
            val targetTime : Long = 1000/fps
            var speed = 2f
            var gameOver = false
            if (asteroids.list.size == 0) {
                asteroids.addAsteroid()
            }
            while (!gameOver) {
                while (start) {
                    updateAsteroidsPosition(speed)
                    invalidate()
                    if (detectAsteroidCollision(spaceship, asteroids.list.first())) {
                        start = false
                        gameOver = true
                    }
                    try {
                        Thread.sleep(targetTime)
                    } catch ( e : Exception) {
                        e.printStackTrace()
                    }
                    if (speed < 20f) {
                        speed += 0.001f
                    }
                }
            }
        })
        thread.start()
    }

    fun updateAsteroidsPosition(speed : Float) {
        asteroids.moveAsteroids(speed)
        if (asteroids.list.last().posY >= asteroidDistance) {
            asteroids.addAsteroid()
            asteroidDistance = (1..400).random(Random(System.nanoTime()))
        }
        if (asteroids.list.first().posY >= 2000) {
            asteroids.removeAsteroid(asteroids.list.first())
        }
    }

    fun detectAsteroidCollision (spaceship: Spaceship, asteroid: Asteroid): Boolean {

        val collisionArea = spaceship.bitmap.width / 2 + asteroid.bitmap.width / 2
        val x = spaceship.posX - asteroid.posX
        val y = spaceship.posY - asteroid.posY
        val distance = sqrt(x * x + y * y )
        return distance <= collisionArea
    }
}

class Spaceship (spaceshipBitmap: Bitmap){

    var posX = 0f
    var posY = 0f
    var bitmap = spaceshipBitmap
    var speed = 5f

}


class Asteroid (asteroidBitmaps: List<Bitmap>){

    val posX = (80..1000).random(Random(System.nanoTime())).toFloat()
    var posY = -100f
    val bitmap = asteroidBitmaps[asteroidBitmaps.indices.random()]

    fun move(speed : Float) {
        posY += speed
    }

}

class Asteroids (private val asteroidBitmaps: List<Bitmap>){

    var list = mutableListOf<Asteroid>()

    fun addAsteroid() {
        list.add(Asteroid(asteroidBitmaps))
    }

    fun removeAsteroid(asteroid: Asteroid){
        list.remove(asteroid)
    }

    fun moveAsteroids(speed : Float) {
        for (asteroid in list) {
            asteroid.move(speed)
        }
    }

}