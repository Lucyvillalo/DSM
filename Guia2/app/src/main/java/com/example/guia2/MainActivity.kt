package com.example.guia2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
class MainActivity : AppCompatActivity() {

    // 1. Variables
    lateinit var btnSaludar: Button
    lateinit var btnLimpiar: Button
    lateinit var etNombre: EditText

    lateinit var etApellido: EditText
    lateinit var tvSaludo: TextView
    lateinit var tvMensaje: TextView

    // 2. Metodo del ciclo de vida del activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. Establecer archivo layout
        setContentView(R.layout.activity_main)

        // 4. Referencias a elementos de la UI
        btnSaludar = findViewById(R.id.btnSaludar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        tvSaludo = findViewById(R.id.tvSaludo)
        tvMensaje = findViewById(R.id.tvMensaje)

        tvSaludo.text = ""
        tvMensaje.text = ""

        // 5. Listeners de los botones
        btnSaludar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            if (nombre.isEmpty()) {
                mostrarToast("Error, debes completar nombre y apellido.")
            }else {
                val calendar = Calendar.getInstance()
                val hora = calendar.get(Calendar.HOUR_OF_DAY)
                val esDia = hora in 6..17

                val saludoHorario = if (esDia) "Hola buenos días" else "Hola buenas noches"
                val nombreCompleto = "$nombre $apellido"

                tvSaludo.text = "$saludoHorario, $nombreCompleto"
                tvMensaje.text = "Bienvenido al laboratorio 2 de DSM441."
                mostrarToast("Saludo generado exitosamente")
            }
        }

        btnLimpiar.setOnClickListener {
            etNombre.text.clear()
            tvSaludo.text = ""
            tvMensaje.text = ""
            etNombre.requestFocus()
            mostrarToast("Pantalla reiniciada")
        }
    }

    // Funcion auxiliar para mostrar mensaje emergente
    private fun mostrarToast(
        mensaje: String,
        duracion: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(
            this,
            mensaje,
            duracion
        ).show()
    }
}