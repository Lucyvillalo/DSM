package com.example.todocompose.repository

import android.content.Context

/**
 * Guarda localmente los IDs de las tareas creadas desde esta app, para que
 * solo esas se puedan completar, modificar o eliminar (la API es compartida).
 */
class OwnedTodoStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("owned_todos", Context.MODE_PRIVATE)

    fun ids(): Set<String> = prefs.getStringSet(KEY, emptySet())?.toSet() ?: emptySet()

    fun add(id: String) {
        prefs.edit().putStringSet(KEY, ids() + id).apply()
    }

    fun remove(id: String) {
        prefs.edit().putStringSet(KEY, ids() - id).apply()
    }

    private companion object {
        const val KEY = "ids"
    }
}
