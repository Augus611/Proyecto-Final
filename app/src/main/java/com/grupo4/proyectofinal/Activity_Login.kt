package com.grupo4.proyectofinal

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Activity_Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    //Metodo registrar
    fun Registrar(view: View?) {
        val registrar = Intent(this, Registrar::class.java)
        startActivity(registrar)
    }

}