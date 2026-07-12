package com.example.activity_form

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var etNombres: EditText
    lateinit var etApellidos: EditText
    lateinit var etCorreo: EditText
    lateinit var etTelefono: EditText
    lateinit var etEdad: EditText
    lateinit var btnValidar: Button
    lateinit var tvResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Referencia al archivo layout
        setContentView(R.layout.activity_main)

        // 2. Referencias a las vistas
        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        etCorreo = findViewById(R.id.etCorreo)
        etTelefono = findViewById(R.id.etTelefono)
        etEdad = findViewById(R.id.etEdad)
        btnValidar = findViewById(R.id.btnValidar)
        tvResultado = findViewById(R.id.tvResultado)

        // 3. Listener del boton Validar
        btnValidar.setOnClickListener {
            validarFormulario()
        }

        // 4. Recuperar estado guardado (si existe)
        if (savedInstanceState != null) {
            etNombres.setText(savedInstanceState.getString("NOMBRES", ""))
            etApellidos.setText(savedInstanceState.getString("APELLIDOS", ""))
            etCorreo.setText(savedInstanceState.getString("CORREO", ""))
            etTelefono.setText(savedInstanceState.getString("TELEFONO", ""))
            etEdad.setText(savedInstanceState.getString("EDAD", ""))
            tvResultado.text = savedInstanceState.getString("RESULTADO", "")
        }
    }

    private fun validarFormulario() {
        val nombres = etNombres.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val edadTexto = etEdad.text.toString().trim()

        // 1. Validar que todos los campos esten completos
        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() ||
            telefono.isEmpty() || edadTexto.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validar que la edad sea numerica
        val edad = edadTexto.toIntOrNull()
        if (edad == null) {
            Toast.makeText(this, "La edad debe ser un valor numérico", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Validar mayoria de edad
        val esMayorDeEdad = edad >= 18
        val mensajeEdad = if (esMayorDeEdad) "Es mayor de edad" else "Es menor de edad"

        // 4. Mostrar resumen en el TextView
        val resumen = "Resumen de la información ingresada:\n\n" +
                "Nombres: $nombres\n" +
                "Apellidos: $apellidos\n" +
                "Correo: $correo\n" +
                "Teléfono: $telefono\n" +
                "Edad: $edad años\n\n" +
                "Resultado: $mensajeEdad"

        tvResultado.text = resumen
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("NOMBRES", etNombres.text.toString())
        outState.putString("APELLIDOS", etApellidos.text.toString())
        outState.putString("CORREO", etCorreo.text.toString())
        outState.putString("TELEFONO", etTelefono.text.toString())
        outState.putString("EDAD", etEdad.text.toString())
        outState.putString("RESULTADO", tvResultado.text.toString())
    }
}