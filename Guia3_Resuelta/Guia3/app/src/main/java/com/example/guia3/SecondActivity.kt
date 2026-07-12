package com.example.guia3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    lateinit var btnRegresar: Button
    lateinit var btnAbrirThirdFromSecond: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Referencia al archivo layout
        setContentView(R.layout.activity_second)

        // 2. Referencias a los botones
        btnRegresar = findViewById(R.id.btnRegresar)
        btnAbrirThirdFromSecond = findViewById(R.id.btnAbrirThirdFromSecond)

        // 3. Registro del observador
        lifecycle.addObserver(MyLifeCycleObserver("SecondActivity"))

        // 4. Listener del boton regresar
        btnRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 5. Listener del boton abrir ThirdActivity
        btnAbrirThirdFromSecond.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
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
            "SecondActivity -> $mensaje",
            duracion
        ).show()
    }
}