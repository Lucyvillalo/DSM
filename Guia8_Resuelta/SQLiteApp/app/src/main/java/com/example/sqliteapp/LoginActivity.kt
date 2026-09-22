package com.example.sqliteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sqliteapp.model.Usuario

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val txtNick = findViewById<EditText>(R.id.txtNick)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)

        findViewById<Button>(R.id.btnIngresar).setOnClickListener {
            val nick = txtNick.text.toString().trim()
            val password = txtPassword.text.toString()
            when {
                nick.isEmpty() -> txtNick.error = "Ingrese su nick"
                password.isEmpty() -> txtPassword.error = "Ingrese su contraseña"
                Usuario(this).use { it.validar(nick, password) } -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                else -> Toast.makeText(this, "Nick o contraseña incorrectos", Toast.LENGTH_LONG).show()
            }
        }

        findViewById<Button>(R.id.btnIrRegistro).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}
