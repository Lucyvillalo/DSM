package com.example.todocompose.ui.theme.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todocompose.data.model.Todo
import java.util.UUID

@Composable
fun TodoList(
    todos: List<Todo>,
    ownedIds: Set<String>,
    onComplete: (Todo) -> Unit,
    onEdit: (Todo) -> Unit,
    onDelete: (Todo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(todos) { todo ->
            TodoItem(todo, todo.id in ownedIds, onComplete, onEdit, onDelete)
        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    canModify: Boolean,
    onComplete: (Todo) -> Unit,
    onEdit: (Todo) -> Unit,
    onDelete: (Todo) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ID: ${todo.id}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (todo.done) "Completada" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (todo.done) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Titulo: ${todo.title}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Descripcion: ${todo.description}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Solo las tareas creadas por el usuario se pueden completar, modificar o eliminar
            if (canModify) {
                if (!todo.done) {
                    Button(
                        onClick = { onComplete(todo) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Marcar como completada")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onEdit(todo) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Modificar") }
                    Button(
                        onClick = { onDelete(todo) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Eliminar") }
                }
            }
        }
    }
}

@Preview
@Composable
fun TodoPreviewScreen() {
    val sampleTodos = listOf(
        Todo(
            id = UUID.randomUUID().toString(),
            title = "Comprar leche",
            description = "Ir al supermercado antes de las 6 PM.",
            done = false
        ),
        Todo(
            id = UUID.randomUUID().toString(),
            title = "Estudiar Kotlin",
            description = "Repasar funciones de alto orden y lambdas.",
            done = true
        ),
        Todo(
            id = UUID.randomUUID().toString(),
            title = "Hacer ejercicio",
            description = "Salir a correr al parque por 30 minutos.",
            done = false
        )
    )
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sampleTodos) { todo ->
            TodoItem(todo, canModify = true, onComplete = {}, onEdit = {}, onDelete = {})
        }
    }
}
