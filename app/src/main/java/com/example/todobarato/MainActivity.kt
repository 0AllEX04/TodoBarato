package com.example.todobarato

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        Log.d(
            "TODO_BARATO",
            "Base de datos abierta correctamente: ${db.isOpen}"
        )

        val cursor = db.rawQuery("SELECT * FROM ventas", null)

        while (cursor.moveToNext()) {

            val codigo =
                cursor.getString(cursor.getColumnIndexOrThrow("codigo"))

            val nombre =
                cursor.getString(cursor.getColumnIndexOrThrow("nombre"))

            val precio =
                cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))

            val cantidad =
                cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))

            val tipo =
                cursor.getString(cursor.getColumnIndexOrThrow("tipo"))

            val fechaVenta =
                cursor.getString(cursor.getColumnIndexOrThrow("fecha_venta"))

            Log.d(
                "TODO_BARATO",
                "$codigo | $nombre | S/$precio | Cantidad: $cantidad | $tipo | $fechaVenta"
            )
        }

        cursor.close()
    }
}