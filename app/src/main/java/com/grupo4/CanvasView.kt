package com.grupo4.proyectofinal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat

class CanvasView (context: Context) : View(context){

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
    var centerX = 0f
    var centerY = 0f
    var sizeX = 450f
    var sizeY = 700f
    var posX = 0f
    var tempPosX = 0f
    var posY = 0f
    var start = false
    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPaint(paintBlack)
        centerX = width / 2f
        centerY = height / 2f
        posY = centerY + 650
        if (!start) {
            posX = centerX
            tempPosX = centerX
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
        canvas?.drawCircle(
            posX,
            posY,
            20f,
            paintWhite.apply {
                style = Paint.Style.FILL
            }
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                start = true
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }





}