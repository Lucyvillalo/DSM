package com.example.todocompose.ui.theme.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todocompose.TodoViewModel
import com.example.todocompose.data.model.Todo

@Composable
fun TodoScreen(viewModel: TodoViewModel) {
    val todos by viewModel.todos.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val ownedIds by viewModel.ownedIds.collectAsState()
    val error by viewModel.error.collectAsState()
    var titleInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var editingTodo by remember { mutableStateOf<Todo?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTodos()
    }
    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mis Tareas",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )
        TextField(
            value = titleInput,
            onValueChange = { titleInput = it },
            label = { Text("Titulo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = descriptionInput,
            onValueChange = { descriptionInput = it },
            label = { Text("Descripcion") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (titleInput.isNotBlank()) {
                    val selectedTodo = editingTodo
                    if (selectedTodo == null) {
                        viewModel.addTodo(titleInput, descriptionInput)
                    } else {
                        viewModel.updateTodo(selectedTodo, titleInput, descriptionInput)
                    }
                    titleInput = ""
                    descriptionInput = ""
                    editingTodo = null
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text(if (editingTodo == null) "Agregar" else "Guardar cambios")
        }
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
            TextButton(
                onClick = { viewModel.loadTodos() },
                enabled = !loading
            ) {
                Text("Reintentar conexión")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (loading) {
            CircularProgressIndicator()
        } else if (todos.isEmpty()) {
            Text(
                text = "No hay tareas guardadas todavía. Agrega una para comenzar.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            TodoList(
                todos = todos,
                ownedIds = ownedIds,
                onComplete = { todo -> viewModel.completeTodo(todo) },
                onEdit = { todo ->
                    editingTodo = todo
                    titleInput = todo.title
                    descriptionInput = todo.description
                },
                onDelete = { todo ->
                    viewModel.deleteTodo(todo)
                    if (editingTodo?.id == todo.id) {
                        editingTodo = null
                        titleInput = ""
                        descriptionInput = ""
                    }
                }
            )
        }
    }
}
