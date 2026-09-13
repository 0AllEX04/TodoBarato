package com.example.todobarato

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val contenedorVentas = findViewById<LinearLayout>(R.id.contenedorVentas)

        // ==================== ♡ANIMACIÓN♡ ====================//
        contenedorVentas.alpha = 0f

        contenedorVentas.animate()
            .alpha(1f)
            .setDuration(5000)
            .start()

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        Log.d(
            "TODO_BARATO",
            "Base de datos abierta correctamente: ${db.isOpen}"
        )

        val cursor = db.rawQuery("SELECT * FROM ventas", null)

        while (cursor.moveToNext()) {

            val codigo = cursor.getString(
                cursor.getColumnIndexOrThrow("codigo")
            )

            val nombre = cursor.getString(
                cursor.getColumnIndexOrThrow("nombre")
            )

            val precio = cursor.getDouble(
                cursor.getColumnIndexOrThrow("precio")
            )

            val cantidad = cursor.getInt(
                cursor.getColumnIndexOrThrow("cantidad")
            )

            val tipo = cursor.getString(
                cursor.getColumnIndexOrThrow("tipo")
            )

            val fechaVenta = cursor.getString(
                cursor.getColumnIndexOrThrow("fecha_venta")
            )

            val tarjeta = TextView(this)

            tarjeta.text = """
                Código: $codigo
                Nombre: $nombre
                Precio: S/$precio
                Cantidad: $cantidad
                Tipo: $tipo
                Fecha: $fechaVenta
            """.trimIndent()

            tarjeta.textSize = 18f
            tarjeta.setPadding(24, 24, 24, 24)

            if (tipo == "Factura") {
                tarjeta.setBackgroundColor(
                    android.graphics.Color.parseColor("#FFCDD2")
                )
            } else if (tipo == "Boleta") {
                tarjeta.setBackgroundColor(
                    android.graphics.Color.parseColor("#C8E6C9")
                )
            }

            contenedorVentas.addView(tarjeta)

            Log.d(
                "TODO_BARATO",
                "$codigo | $nombre | S/$precio | Cantidad: $cantidad | $tipo | $fechaVenta"
            )
        }

        cursor.close()
    }
}