package com.grupo4.proyectofinal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.random.Random

class CanvasView (context: Context, val mainActivity: MainActivity) : View(context){

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
    var spaceship : Bitmap = Bitmap.createScaledBitmap(
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

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPaint(paintBlack)
        centerX = width / 2f
        centerY = height / 2f
        posY = centerY + sizeY - 100
        if (firstStart) {
            posX = centerX
            tempPosX = centerX
        }
        for (asteroid in asteroids.list) {
            canvas?.drawBitmap(
                asteroid.bitmap,
                asteroid.posX,
                asteroid.posY,
                paint
            )
        }
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
            spaceship,
            posX - spaceship.width / 2,
            posY - spaceship.height / 2,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (firstStart) {
                    createAsteroidsThread()
                    firstStart = false
                }
                start = !start
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun createAsteroidsThread() {
        val thread = Thread(Runnable {
            if (asteroids.list.size == 0) {
                asteroids.addAsteroid()
            }
            while (true) { //Despues cambiar esto
                while (start) {
                    asteroids.moveAsteroids()
                    if (asteroids.list.last().posY >= asteroidDistance) {
                        asteroids.addAsteroid()
                        asteroidDistance = (1..400).random(Random(System.nanoTime()))
                    }
                    if (asteroids.list.first().posY >= 2000) {
                        asteroids.removeAsteroid(asteroids.list.first())
                    }
                    Thread.sleep(2)
                }
            }
        })
        thread.start()
    }
}