package com.grupo4.proyectofinal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Activity_Login : AppCompatActivity() {

    lateinit var UsuariosDBHelper: miSqliteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        UsuariosDBHelper = miSqliteHelper(this)

    }

    //Metodo registrar
    fun Registrar(view: View?) {
        val registrar = Intent(this, Registrar::class.java)
        startActivity(registrar)
    }

    fun ValidarUsuario(view: View){
        val inputUsuario = findViewById<EditText>(R.id.input_usuario)
        val inputPass = findViewById<EditText>(R.id.input_password)

        val usuario = inputUsuario.text.toString()
        val pass = inputPass.text.toString()

        val resultado = UsuariosDBHelper.searchUsuario(usuario)

        if (resultado == null) {
            Toast.makeText(this, "El usuario ingresado no existe", Toast.LENGTH_SHORT).show()
        } else {
            val usuarioValidar = resultado.elementAt(0)
            val passValidar = resultado.elementAt(1)

            if (usuario.isNotBlank() && pass.isNotBlank()) {
                if(usuario == usuarioValidar && pass == passValidar){
                    inputUsuario.text.clear()
                    inputPass.text.clear()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("Usuario", usuario)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "Complete los datos", Toast.LENGTH_LONG).show()
            }
        }
    }


}