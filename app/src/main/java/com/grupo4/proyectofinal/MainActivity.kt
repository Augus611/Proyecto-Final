package com.grupo4.proyectofinal

import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Visibility
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var canvasView : CanvasView
    private lateinit var relativeLayout : RelativeLayout
    private lateinit var sensorManager : SensorManager
    private lateinit var accelerometer : Sensor
    lateinit var currentScoreTextView : TextView
    lateinit var newHighscoreTextView : TextView
    lateinit var UsuariosDBHelper: miSqliteHelper
    var usuario : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        canvasView = CanvasView(this, this)
        relativeLayout = RelativeLayout(this)
        var params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        relativeLayout.addView(
            canvasView,
            params
        )
        val newid = View.generateViewId()
        currentScoreTextView = TextView(this)
        currentScoreTextView.apply {
            id = newid
            text = canvasView.currentScore.toInt().toString()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                typeface = resources.getFont(R.font.pressstart_normal)
            }
            setTextColor(resources.getColor(R.color.white))
            gravity = Gravity.CENTER
            textSize = 32f
        }
        params = RelativeLayout.LayoutParams(700, 90)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        params.setMargins(0, windowManager.defaultDisplay.height/2 - canvasView.sizeY.toInt() - 80, 0, 0)
        relativeLayout.addView(
            currentScoreTextView,
            params
        )
        val loginButton = ImageButton(this)
        loginButton.setBackgroundColor(Color.TRANSPARENT)
        loginButton.setImageResource(R.drawable.spaceship)
        loginButton.setOnClickListener{
            startActivityForResult(
                Intent(this, Activity_Login::class.java),
                1
            )
        }
        params = RelativeLayout.LayoutParams(100, 100)
        params.addRule(RelativeLayout.LEFT_OF, currentScoreTextView.id)
        params.addRule(RelativeLayout.ALIGN_TOP, currentScoreTextView.id)
        params.addRule(RelativeLayout.ALIGN_BOTTOM, currentScoreTextView.id)
        relativeLayout.addView(
            loginButton,
            params
        )
        val highscoreButton = ImageButton(this)
        highscoreButton.setImageResource(R.drawable.star)
        highscoreButton.setBackgroundColor(Color.TRANSPARENT)
        highscoreButton.setOnClickListener{
            startActivity(
                Intent(this, PuntajesMaximos::class.java).putExtra("Usuario", usuario)
            )
        }
        params = RelativeLayout.LayoutParams(100, 100)
        params.addRule(RelativeLayout.RIGHT_OF, currentScoreTextView.id)
        params.addRule(RelativeLayout.ALIGN_TOP, currentScoreTextView.id)
        params.addRule(RelativeLayout.ALIGN_BOTTOM, currentScoreTextView.id)
        relativeLayout.addView(
            highscoreButton,
            params
        )
        newHighscoreTextView = TextView(this)
        newHighscoreTextView.apply {
            text = "¡Nueva puntuación máxima!"
            visibility = View.INVISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                typeface = resources.getFont(R.font.pressstart_normal)
            }
            setTextColor(resources.getColor(R.color.white))
            gravity = Gravity.CENTER
            textSize = 16f
        }
        params = RelativeLayout.LayoutParams(1000, 180)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        params.addRule(RelativeLayout.BELOW, currentScoreTextView.id)
        params.setMargins(0, 50, 0, 0)
        relativeLayout.addView(
            newHighscoreTextView,
            params)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setFullscreen()
        setContentView(relativeLayout)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_UI)

        UsuariosDBHelper = miSqliteHelper(this)

    }

    override fun onResume() {
        super.onResume()
        setFullscreen()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        canvasView.start = false
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (canvasView.start) {
            currentScoreTextView.text = canvasView.currentScore.toInt().toString()
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
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onAccuracyChanged(event: Sensor?, p1: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            usuario = data?.getStringExtra("Usuario")
            if (canvasView.gameOver) {
                modificarPuntaje(canvasView.currentScore.toInt())
            }
        }
    }

    fun modificarPuntaje(score: Int){

        if (usuario != null) {
            val resultado = UsuariosDBHelper.searchUsuario(usuario!!)
            val Puntuacion = score
            val PuntuacionMaxActual = resultado?.elementAt(2)!!.toInt()

            if(PuntuacionMaxActual.toString().isNotBlank()){
                if ( Puntuacion > PuntuacionMaxActual ){
                    UsuariosDBHelper.ActualizarDato(usuario!!, score)
                    val thread = Thread {
                        runOnUiThread {
                            newHighscoreTextView.visibility = View.VISIBLE
                        }
                        Thread.sleep(3000)
                        runOnUiThread {
                            newHighscoreTextView.visibility = View.INVISIBLE
                        }
                    }
                    thread.start()
                }
            }
        }
    }


}
