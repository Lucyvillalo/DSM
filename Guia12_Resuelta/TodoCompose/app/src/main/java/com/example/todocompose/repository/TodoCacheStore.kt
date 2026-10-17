package com.example.todocompose.repository

import android.content.Context
import com.example.todocompose.data.model.Todo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TodoCacheStore(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences("todo_cache", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val todoListType = object : TypeToken<List<Todo>>() {}.type

    @Synchronized
    fun todos(): List<Todo> = runCatching {
        gson.fromJson<List<Todo>>(preferences.getString(TODOS_KEY, "[]"), todoListType)
    }.getOrNull().orEmpty()

    @Synchronized
    fun deletedIds(): Set<String> =
        preferences.getStringSet(DELETED_IDS_KEY, emptySet())?.toSet().orEmpty()

    @Synchronized
    fun save(todo: Todo) {
        val updated = todos().filterNot { it.id == todo.id } + todo
        preferences.edit()
            .putString(TODOS_KEY, gson.toJson(updated))
            .putStringSet(DELETED_IDS_KEY, deletedIds() - todo.id)
            .apply()
    }

    @Synchronized
    fun clearLocal(todoId: String) {
        preferences.edit()
            .putString(TODOS_KEY, gson.toJson(todos().filterNot { it.id == todoId }))
            .apply()
    }

    @Synchronized
    fun confirmDeleted(todoId: String) {
        preferences.edit()
            .putString(TODOS_KEY, gson.toJson(todos().filterNot { it.id == todoId }))
            .putStringSet(DELETED_IDS_KEY, deletedIds() - todoId)
            .apply()
    }

    @Synchronized
    fun markDeleted(todoId: String) {
        preferences.edit()
            .putString(TODOS_KEY, gson.toJson(todos().filterNot { it.id == todoId }))
            .putStringSet(DELETED_IDS_KEY, deletedIds() + todoId)
            .apply()
    }

    private companion object {
        const val TODOS_KEY = "todos"
        const val DELETED_IDS_KEY = "deleted_ids"
    }
}
