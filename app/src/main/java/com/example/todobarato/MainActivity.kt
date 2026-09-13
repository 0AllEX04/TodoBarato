package com.example.todobarato

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.drawable.GradientDrawable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val contenedorVentas = findViewById<LinearLayout>(R.id.contenedorVentas)
        val txtResumen = findViewById<TextView>(R.id.txtResumen)
        val txtTipos = findViewById<TextView>(R.id.txtTipos)
        val txtTotalDinero = findViewById<TextView>(R.id.txtTotalDinero)

        // ==================== ♡ANIMACIÓN♡ ====================//
        contenedorVentas.alpha = 0f
        contenedorVentas.translationY = 300f

        contenedorVentas.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(5000)
            .start()
        // ==================== ♡ANIMACIÓN♡ ====================//

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        Log.d(
            "TODO_BARATO",
            "Base de datos abierta correctamente: ${db.isOpen}"
        )

        val cursor = db.rawQuery("SELECT * FROM ventas", null)
        txtResumen.text = "Total de ventas: ${cursor.count}"
        var totalFacturas = 0
        var totalBoletas = 0
        var totalVendido = 0.0

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
            totalVendido += precio * cantidad

            val tipo = cursor.getString(
                cursor.getColumnIndexOrThrow("tipo")
            )

            if (tipo == "Factura") {
                totalFacturas++
            } else if (tipo == "Boleta") {
                totalBoletas++
            }

            val fechaVenta = cursor.getString(
                cursor.getColumnIndexOrThrow("fecha_venta")
            )

            val tarjeta = TextView(this)

            tarjeta.text = """
            $nombre
            Código: $codigo
            Precio: S/ $precio
            Cantidad: $cantidad
            [$tipo]
            Fecha de venta: $fechaVenta
            """.trimIndent()

            tarjeta.textSize = 17f
            tarjeta.setTextColor(android.graphics.Color.parseColor("#212121"))
            tarjeta.setPadding(24, 24, 24, 24)

            // Diseño de la tarjeta
            val fondo = GradientDrawable()

            fondo.cornerRadius = 24f

            if (tipo == "Factura") {
                fondo.setColor(
                    android.graphics.Color.parseColor("#FFCDD2")
                )
            } else if (tipo == "Boleta") {
                fondo.setColor(
                    android.graphics.Color.parseColor("#C8E6C9")
                )
            }

            tarjeta.background = fondo

            val parametros = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            parametros.setMargins(0, 0, 0, 24)

            tarjeta.layoutParams = parametros

            contenedorVentas.addView(tarjeta)

            Log.d(
                "TODO_BARATO",
                "$codigo | $nombre | S/$precio | Cantidad: $cantidad | $tipo | $fechaVenta"
            )
        }
        txtTipos.text = "Facturas: $totalFacturas | Boletas: $totalBoletas"

        txtTotalDinero.text = "Total vendido: S/ %.2f".format(totalVendido)

        cursor.close()
    }
}