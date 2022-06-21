package com.grupo4.proyectofinal

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class Registrar : AppCompatActivity() {

    lateinit var UsuariosDBHelper: miSqliteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        UsuariosDBHelper = miSqliteHelper(this)

    }

    fun crearUsuario(view: View){
        val inputUsuario = findViewById<EditText>(R.id.input_usuarioRegistrar)
        val inputPass = findViewById<EditText>(R.id.input_passwordRegistrar)
        val inputPassValidar = findViewById<EditText>(R.id.input_passwordValidar)
        val puntuacion = 0

        val usuario = inputUsuario.text.toString()
        val pass = inputPass.text.toString()
        val passValidar = inputPassValidar.text.toString()

        if (UsuariosDBHelper.searchUsuario(usuario) == null) {
            if (usuario.isNotBlank() && pass.isNotBlank() && passValidar.isNotBlank()) {
                if(pass == passValidar){
                    UsuariosDBHelper.addDato(usuario, pass, puntuacion)

                    inputUsuario.text.clear()
                    inputPass.text.clear()
                    inputPassValidar.text.clear()

                    Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Las contraseñas son diferentes", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "Complete los datos", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "El usuario ingresado ya existe", Toast.LENGTH_SHORT).show()
        }
    }
}