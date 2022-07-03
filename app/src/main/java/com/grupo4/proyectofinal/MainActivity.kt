package com.grupo4.proyectofinal

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
    lateinit var UsuariosDBHelper: miSqliteHelper

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
        loginButton.background = AppCompatResources.getDrawable(this, R.drawable.round_button)
        loginButton.setImageResource(R.drawable.spaceship)
        loginButton.setOnClickListener{
            startActivity(
                Intent(this, Activity_Login::class.java)
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
        val configButton = ImageButton(this)
        configButton.background = AppCompatResources.getDrawable(this, R.drawable.round_button)
        params = RelativeLayout.LayoutParams(100, 100)
        params.addRule(RelativeLayout.RIGHT_OF, currentScoreTextView.id)
        params.addRule(RelativeLayout.ALIGN_TOP, currentScoreTextView.id)
        params.addRule(RelativeLayout.ALIGN_BOTTOM, currentScoreTextView.id)
        relativeLayout.addView(
            configButton,
            params
        )
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

    fun ModificarPuntaje(Score: Int){

        val bundle = intent.extras
        val UsuarioNombre = bundle?.getString("Usuario").toString()
        val resultado = UsuariosDBHelper.searchUsuario(UsuarioNombre)

        val Puntuacion = Score
        val PuntuacionMaxActual = resultado?.elementAt(2)!!.toInt()

        if(UsuarioNombre.isNotBlank() && PuntuacionMaxActual.toString().isNotBlank()){

            if ( Puntuacion > PuntuacionMaxActual ){
                UsuariosDBHelper.ActualizarDato(UsuarioNombre, PuntuacionMaxActual.toInt())
            }

            Toast.makeText(this, "Nuevo puntaje maximo", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this, "No se pudo actualizar exitosamente", Toast.LENGTH_LONG).show()
        }
    }


}
