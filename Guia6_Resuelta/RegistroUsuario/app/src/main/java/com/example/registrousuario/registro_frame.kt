package com.example.registrousuario

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroFrameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_frame)
        configurarFormulario()
    }

    private fun configurarFormulario() {
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val rgGenero = findViewById<RadioGroup>(R.id.rgGenero)
        val cbTerminos = findViewById<CheckBox>(R.id.cbTerminos)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val generoId = rgGenero.checkedRadioButtonId

            when {
                nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || contrasena.isEmpty() ->
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                generoId == -1 ->
                    Toast.makeText(this, "Selecciona un género", Toast.LENGTH_SHORT).show()
                !cbTerminos.isChecked ->
                    Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
                else -> {
                    val genero = findViewById<RadioButton>(generoId).text.toString()
                    Toast.makeText(this, "Registro exitoso: $nombre $apellido ($genero)", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}