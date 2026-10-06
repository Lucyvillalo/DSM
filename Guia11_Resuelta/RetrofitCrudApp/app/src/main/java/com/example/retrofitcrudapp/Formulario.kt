package com.example.retrofitcrudapp

import android.widget.EditText

/** Evita enviar campos vacíos o cerrar la app al convertir una edad inválida. */
internal fun validarFormulario(nombre: EditText, apellido: EditText, edad: EditText): Boolean {
    nombre.error = null
    apellido.error = null
    edad.error = null
    var valido = true
    if (nombre.text.toString().isBlank()) { nombre.error = "Ingrese el nombre"; valido = false }
    if (apellido.text.toString().isBlank()) { apellido.error = "Ingrese el apellido"; valido = false }
    val numero = edad.text.toString().toIntOrNull()
    if (numero == null || numero < 0) { edad.error = "Ingrese una edad válida"; valido = false }
    return valido
}
