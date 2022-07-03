package com.grupo4.proyectofinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class PuntajesMaximos : AppCompatActivity() {

    lateinit var usuariosDBHelper: miSqliteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puntajes_maximos)

        usuariosDBHelper = miSqliteHelper(this)
        mostrarPuntajes()

    }

    fun mostrarPuntajes(){
        val nombreUsuario = findViewById<TextView>(R.id.textView3)
        val puntaje = findViewById<TextView>(R.id.textView4)
        val puntuaciones = usuariosDBHelper.getPuntuaciones()
        if (puntuaciones.isNotEmpty()) {
            for (usuario in puntuaciones) {
                if (usuario.isNotEmpty()) {
                    val prevName = nombreUsuario.text
                    val newName = prevName.toString() + "\n" + usuario[0]
                    nombreUsuario.text = newName
                    val prevScore = puntaje.text
                    val newScore = prevScore.toString() + "\n" + usuario[1]
                    puntaje.text = newScore
                }
            }
        }
    }
}