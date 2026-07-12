package com.example.guia2


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var btnSaludar: Button
    lateinit var btnLimpiar: Button
    lateinit var etNombre: EditText
    lateinit var etApellido: EditText
    lateinit var tvSaludo: TextView
    lateinit var tvMensaje: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSaludar = findViewById(R.id.btnSaludar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        tvSaludo = findViewById(R.id.tvSaludo)
        tvMensaje = findViewById(R.id.tvMensaje)

        tvSaludo.text = ""
        tvMensaje.text = ""

        btnSaludar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty()) {
                mostrarToast("Error, debes completar nombre y apellido.")
            } else {
                val calendar = Calendar.getInstance()
                val hora = calendar.get(Calendar.HOUR_OF_DAY)
                val esDia = hora in 6..17
                val saludoHorario = if (esDia) "Hola buenos días" else "Hola buenas noches"

                tvSaludo.text = "$saludoHorario, $nombre $apellido"
                tvMensaje.text = "Bienvenido al laboratorio 2 de DSM441."
                mostrarToast("Saludo generado exitosamente")
            }
        }

        btnLimpiar.setOnClickListener {
            etNombre.text.clear()
            etApellido.text.clear()
            tvSaludo.text = ""
            tvMensaje.text = ""
            etNombre.requestFocus()
            mostrarToast("Pantalla reiniciada")
        }
    }

    private fun mostrarToast(mensaje: String, duracion: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, mensaje, duracion).show()
    }
}