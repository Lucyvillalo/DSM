package com.example.sqliteapp.model

import android.content.ContentValues
import android.content.Context
import com.example.sqliteapp.db.HelperDB

class Usuario(context: Context?) : java.io.Closeable {
    private val helper = HelperDB(context)
    private val db = helper.writableDatabase

    companion object {
        const val TABLE_NAME_USUARIO = "usuario"
        const val COL_ID = "idusuario"
        const val COL_NICK = "nick"
        const val COL_PASSWORD = "contrasena"
        const val CREATE_TABLE_USUARIO =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME_USUARIO(" +
                "$COL_ID integer primary key autoincrement," +
                "$COL_NICK varchar(50) NOT NULL UNIQUE," +
                "$COL_PASSWORD varchar(100) NOT NULL);"
    }

    fun registrar(nick: String, contrasena: String): Long = db.insert(
        TABLE_NAME_USUARIO, null,
        ContentValues().apply { put(COL_NICK, nick); put(COL_PASSWORD, contrasena) }
    )

    fun validar(nick: String, contrasena: String): Boolean = db.query(
        TABLE_NAME_USUARIO, arrayOf(COL_ID), "$COL_NICK=? AND $COL_PASSWORD=?",
        arrayOf(nick, contrasena), null, null, null
    ).use { it.moveToFirst() }
    override fun close() = helper.close()
}
