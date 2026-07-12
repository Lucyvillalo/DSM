package com.example.guia3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ThirdActivity : AppCompatActivity() {

    lateinit var btnRegresarMain: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Referencia al archivo layout
        setContentView(R.layout.activity_third)
        // 2. Referencia al boton
        btnRegresarMain = findViewById(R.id.btnRegresarMain)
        // 3. Registro del observador
        lifecycle.addObserver(MyLifeCycleObserver("ThirdActivity"))
        // 4. Listener del boton
        btnRegresarMain.setOnClickListener {
            // 5. Uso de un intent explicito para iniciar una nueva Activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        mostrarToast("onCreate")
    }

    override fun onStart() {
        super.onStart()
        mostrarToast("onStart")
    }

    override fun onResume() {
        super.onResume()
        mostrarToast("onResume")
    }

    override fun onPause() {
        super.onPause()
        mostrarToast("onPause")
    }

    override fun onStop() {
        super.onStop()
        mostrarToast("onStop")
    }

    override fun onRestart() {
        super.onRestart()
        mostrarToast("onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        mostrarToast("onDestroy")
    }

    private fun mostrarToast(
        mensaje: String,
        duracion: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(
            this,
            "ThirdActivity -> $mensaje",
            duracion
        ).show()
    }
}