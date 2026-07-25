package com.example.miperfil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity 1: Pantalla de Bienvenida
 * Su única responsabilidad es dar la bienvenida al usuario y
 * navegar hacia la pantalla de Registro de Perfil.
 */
class MainActivity : AppCompatActivity() {

    // Constante TAG para identificar los logs de esta Activity
    private val TAG = "MiPerfil-Bienvenida"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(TAG, "onCreate: Pantalla de bienvenida iniciada")

        // 1. Referenciar el botón Iniciar
        val btnIniciar: Button = findViewById(R.id.btnIniciar)

        // 2. Configurar el listener del botón Iniciar
        btnIniciar.setOnClickListener {
            Log.i(TAG, "Botón Iniciar presionado, navegando a RegistroActivity")

            // 3. Crear Intent explícito hacia RegistroActivity
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}
