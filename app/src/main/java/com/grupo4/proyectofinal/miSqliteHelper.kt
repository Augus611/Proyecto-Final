package com.grupo4.proyectofinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class miSqliteHelper(context: Context):SQLiteOpenHelper(context, "usuarios.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val ordenCreacion = "CREATE TABLE usuarios" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT, pass TEXT, puntuacionMaxima INTEGER)"
        db!!.execSQL(ordenCreacion)
    }

    override fun onUpgrade(db: SQLiteDatabase?, olderVersion: Int, newVersion: Int) {
        val ordenBorrado = "DROP TABLE IF EXISTS usuarios"
        db!!.execSQL(ordenBorrado)
        onCreate(db)
    }

    fun addDato(nombre: String, pass: String, puntuacionMaxima: Int){
        val datos = ContentValues()
        datos.put("nombre", nombre)
        datos.put("pass", pass)
        datos.put("puntuacionMaxima", puntuacionMaxima)

        val db = this.writableDatabase
        db.insert("usuarios",null,datos)
        db.close()
    }

    fun searchUsuario(nombre: String): List<String>? {
        val db = this.readableDatabase
        val fila = db.rawQuery("SELECT nombre, pass, puntuacionMaxima FROM usuarios where nombre = ?", arrayOf(nombre))
        return if (fila.moveToFirst()) {
            listOf(fila.getString(0), fila.getString(1), fila.getString(2))
        } else {
            null
        }
    }

    fun ActualizarDato(nombre: String, puntuacionMaxima: Int){

        val arg = arrayOf(nombre.toString())

        val datos = ContentValues()
        datos.put("puntuacionMaxima", puntuacionMaxima)

        val db = this.writableDatabase
        db.update("usuarios", datos,"nombre = ?", arg )
        db.close()
    }

}