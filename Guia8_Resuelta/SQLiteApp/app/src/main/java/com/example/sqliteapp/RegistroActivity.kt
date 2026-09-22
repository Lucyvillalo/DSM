package com.example.sqliteapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sqliteapp.model.Usuario

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val txtNick = findViewById<EditText>(R.id.txtNickRegistro)
        val txtPassword = findViewById<EditText>(R.id.txtPasswordRegistro)
        val txtConfirmar = findViewById<EditText>(R.id.txtConfirmarPassword)

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            val nick = txtNick.text.toString().trim()
            val password = txtPassword.text.toString()
            val confirmar = txtConfirmar.text.toString()
            when {
                nick.isEmpty() -> txtNick.error = "Ingrese un nick"
                password.isEmpty() -> txtPassword.error = "Ingrese una contraseña"
                password.length < 4 -> txtPassword.error = "Use al menos 4 caracteres"
                password != confirmar -> txtConfirmar.error = "Las contraseñas no coinciden"
                Usuario(this).use { it.registrar(nick, password) } == -1L ->
                    Toast.makeText(this, "No se pudo registrar. El nick puede estar en uso", Toast.LENGTH_LONG).show()
                else -> {
                    Toast.makeText(this, "Usuario registrado. Ya puede iniciar sesión", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
    }
}
