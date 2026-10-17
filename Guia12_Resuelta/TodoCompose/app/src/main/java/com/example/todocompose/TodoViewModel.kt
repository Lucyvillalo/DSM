package com.example.todocompose

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todocompose.data.model.Todo
import com.example.todocompose.repository.OwnedTodoStore
import com.example.todocompose.repository.TodoCacheStore
import com.example.todocompose.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.UUID

class TodoViewModel(
    private val repository: TodoRepository,
    private val ownedStore: OwnedTodoStore,
    private val cacheStore: TodoCacheStore
) : ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _ownedIds = MutableStateFlow(ownedStore.ids())
    val ownedIds: StateFlow<Set<String>> = _ownedIds

    fun loadTodos() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val deletedIds = cacheStore.deletedIds()
                val cached = cacheStore.todos().associateBy { it.id }
                val remote = repository.fetchTodos().filterNot { it.id in deletedIds }
                val merged = remote.map { cached[it.id] ?: it }.toMutableList()
                val remoteIds = remote.mapTo(mutableSetOf()) { it.id }
                cached.values.filterNot { it.id in deletedIds || it.id in remoteIds }.forEach(merged::add)
                _todos.value = merged
                _ownedIds.value += merged.map { it.id }
            } catch (e: Exception) {
                reportError(e)
                val localTodos = cacheStore.todos().filterNot { it.id in cacheStore.deletedIds() }
                _todos.value = localTodos
                _ownedIds.value += localTodos.map { it.id }
            } finally {
                _loading.value = false
            }
        }
    }

    fun addTodo(title: String, description: String) {
        if (title.isBlank()) return
        val newTodo = Todo(UUID.randomUUID().toString(), title.trim(), description.trim(), false)
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = repository.addTodo(newTodo)
                ownedStore.add(result.id)
                _ownedIds.value += result.id
                cacheStore.clearLocal(result.id)
                _todos.value = _todos.value + result
            } catch (e: Exception) {
                cacheStore.save(newTodo)
                ownedStore.add(newTodo.id)
                _ownedIds.value += newTodo.id
                _todos.value = _todos.value + newTodo
                reportError(e, "La tarea se guard\u00f3 en este dispositivo.")
            } finally {
                _loading.value = false
            }
        }
    }

    fun completeTodo(todo: Todo) {
        if (todo.id !in _ownedIds.value) return
        replaceTodo(todo.copy(done = true))
    }

    fun updateTodo(todo: Todo, title: String, description: String) {
        if (title.isBlank() || todo.id !in _ownedIds.value) return
        replaceTodo(todo.copy(title = title.trim(), description = description.trim()))
    }

    fun deleteTodo(todo: Todo) {
        if (todo.id !in _ownedIds.value) return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.deleteTodo(todo.id)
                cacheStore.confirmDeleted(todo.id)
            } catch (e: Exception) {
                cacheStore.markDeleted(todo.id)
                reportError(e, "La tarea se elimin\u00f3 de este dispositivo.")
            } finally {
                ownedStore.remove(todo.id)
                _ownedIds.value -= todo.id
                _todos.value = _todos.value.filterNot { it.id == todo.id }
                _loading.value = false
            }
        }
    }

    private fun replaceTodo(updated: Todo) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = repository.updateTodo(updated)
                cacheStore.clearLocal(result.id)
                _todos.value = _todos.value.map { if (it.id == result.id) result else it }
            } catch (e: Exception) {
                cacheStore.save(updated)
                _todos.value = _todos.value.map { if (it.id == updated.id) updated else it }
                reportError(e, "Los cambios se guardaron en este dispositivo.")
            } finally {
                _loading.value = false
            }
        }
    }

    private fun reportError(e: Exception, fallbackMessage: String? = null) {
        val status = (e as? HttpException)?.code()
        val detail = if (status != null) "HTTP $status" else "sin conexi\u00f3n"
        Log.e("TodoViewModel", detail + ": " + e.message, e)
        _error.value = if (fallbackMessage == null) {
            "No se pudo cargar la API ($detail). Se muestran las tareas de este dispositivo."
        } else {
            "API no disponible ($detail). $fallbackMessage"
        }
    }
}
