package com.example.sqliteapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sqliteapp.model.Categoria
import com.example.sqliteapp.model.Productos
import com.example.sqliteapp.model.Usuario

class HelperDB(context: Context?) :
SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

companion object {
private const val DB_NAME = "tienda.sqlite"
private const val DB_VERSION = 4
}

override fun onCreate(db: SQLiteDatabase) {
db.execSQL(Categoria.CREATE_TABLE_CATEGORIA)
db.execSQL(Productos.CREATE_TABLE_PRODUCTOS)
db.execSQL(Usuario.CREATE_TABLE_USUARIO)
}

override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
// Agregar Usuario sin borrar las tablas ni los registros de la práctica.
if (oldVersion < 4) {
db.execSQL(Usuario.CREATE_TABLE_USUARIO)
}
}

}
