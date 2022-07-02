package com.grupo4.proyectofinal

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.math.sqrt
import kotlin.random.Random

class CanvasView(context: Context, val mainActivity: MainActivity) : View(context){

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
    private val colorRed = ResourcesCompat.getColor(resources, R.color.red, null)
    private val paintMargins = Paint().apply {
        color = colorBlack
        style = Paint.Style.FILL
    }
    private val paint = Paint().apply {
        isFilterBitmap = false
    }
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
    val pauseBitmap : Bitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(resources, R.drawable.pause, null),
        256,
        256,
        false
    )
    var asteroids = Asteroids(asteroidBitmaps)
    var asteroidDistance = 100
    var spaceship = Spaceship(spaceshipBitmap)
    var sizeChanged = false
    var gameOver = false
    var currentScore = 0f

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPaint(paintBlack)
        centerX = width / 2f
        centerY = height / 2f
        spaceship.posY = centerY + sizeY - 100
        if (firstStart) {
            spaceship.posX = centerX
            tempPosX = centerX
        }
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
        //Márgenes
        //Izquierdo
        canvas?.drawRect(
            0f,
            0f,
            centerX - sizeX,
            height.toFloat(),
            paintMargins
        )
        //Superior
        canvas?.drawRect(
            0f,
            0f,
            width.toFloat(),
            centerY - sizeY,
            paintMargins
        )
        //Derecho
        canvas?.drawRect(
            centerX + sizeX,
            0f,
            width.toFloat(),
            height.toFloat(),
            paintMargins
        )
        //Inferior
        canvas?.drawRect(
            0f,
            centerY + sizeY,
            width.toFloat(),
            height.toFloat(),
            paintMargins
        )
        //Marco blanco
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
        if (!firstStart and !start and !gameOver) {
            canvas?.drawBitmap(
                pauseBitmap,
                centerX - pauseBitmap.width / 2,
                centerY - pauseBitmap.height / 2,
                paint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (gameOver) {
                    asteroids.list.clear()
                    firstStart = true
                    gameOver = false
                    invalidate()
                    start = false
                }
                if (firstStart) {
                    paintMargins.color = colorBlack
                    createGameThread()
                    firstStart = false
                }
                start = !start
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun createGameThread() {
        val thread = Thread(Runnable {
            currentScore = 0f
            tempPosX = centerX
            val fps : Long = 60
            val targetTime : Long = 1000/fps
            var speed = 2f
            if (asteroids.list.size == 0) {
                asteroids.addAsteroid()
            }
            while (!gameOver) {
                while (start) {
                    updateAsteroidsPosition(speed)
                    invalidate()
                    currentScore += 0.05f
                    mainActivity.currentScoreTextView.text = currentScore.toInt().toString()
                    for (asteroid in asteroids.list) {
                        if (detectAsteroidCollision(spaceship, asteroid)) {
                            start = false
                            gameOver = true
                            vibrate()
                            paintMargins.color = colorRed
                            invalidate()
                        }
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        sizeChanged = true
    }

    private fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
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
    var posY = 0f
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