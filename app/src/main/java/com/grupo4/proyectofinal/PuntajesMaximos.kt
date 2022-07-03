package com.grupo4.proyectofinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class PuntajesMaximos : AppCompatActivity() {

    lateinit var UsuariosDBHelper: miSqliteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puntajes_maximos)

        UsuariosDBHelper = miSqliteHelper(this)
        mostrarPuntaje()

    }

    fun mostrarPuntaje(){
        val nombreUsuario = findViewById<TextView>(R.id.textView3)
        val puntaje = findViewById<TextView>(R.id.textView4)

        val resultado = UsuariosDBHelper.searchUsuario(usuario)

        nombreUsuario.text = resultado?.elementAt(0)
        puntaje.text = resultado?.elementAt(2)
    }

}