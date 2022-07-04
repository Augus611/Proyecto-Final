package com.grupo4.proyectofinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Perfil : AppCompatActivity() {

    lateinit var usuariosDBHelper: miSqliteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usuariosDBHelper = miSqliteHelper(this)
        setContentView(R.layout.activity_perfil)
        val nombreTextView = findViewById<TextView>(R.id.NombreUsuario)
        val usuario = intent.getStringExtra("Usuario")
        nombreTextView.text = usuario
        val puntajeTextView = findViewById<TextView>(R.id.PuntajeMaximo)
        val puntaje = usuariosDBHelper.getPuntuacion(usuario.toString())
        puntajeTextView.text = puntaje
        val cerrarSesionButton = findViewById<Button>(R.id.button4)
        cerrarSesionButton.setOnClickListener{
            setResult(2)
            finish()
        }
    }
}