package com.example.retrofitcrudapp

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.JsonParseException

/** Solo normaliza la lectura: las escrituras siguen enviando edades numéricas. */
object ApiJson {
    private fun texto(json: JsonObject, campo: String): String =
        json.get(campo)?.takeIf { it.isJsonPrimitive }?.asString.orEmpty()
    private fun id(json: JsonObject): Int = texto(json, "id").toIntOrNull()
        ?.takeIf { it > 0 } ?: throw JsonParseException("ID de registro inválido")
    private fun edad(json: JsonObject): Int? = texto(json, "edad").trim().toIntOrNull()?.takeIf { it >= 0 }
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Alumno::class.java, JsonDeserializer<Alumno> { json, _, _ ->
            val dato = json.asJsonObject
            Alumno(id(dato), texto(dato, "nombre"), texto(dato, "apellido"), edad(dato))
        })
        .registerTypeAdapter(Profesor::class.java, JsonDeserializer<Profesor> { json, _, _ ->
            val dato = json.asJsonObject
            Profesor(id(dato), texto(dato, "nombre"), texto(dato, "apellido"), edad(dato))
        }).create()
}
