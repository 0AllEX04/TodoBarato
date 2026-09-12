package com.example.todobarato

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Todo_barato.db"
        private const val DATABASE_VERSION = 1
    }

    init {
        copiarBaseDeDatos()
    }

    private fun copiarBaseDeDatos() {

        val archivoBD = context.getDatabasePath(DATABASE_NAME)

        if (!archivoBD.exists()) {

            archivoBD.parentFile?.mkdirs()

            context.assets.open(DATABASE_NAME).use { entrada ->

                FileOutputStream(archivoBD).use { salida ->

                    entrada.copyTo(salida)
                }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
    }
}