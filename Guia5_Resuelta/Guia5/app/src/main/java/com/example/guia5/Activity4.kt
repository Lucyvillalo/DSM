package com.example.guia5

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Activity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuatro)

        findViewById<Button>(R.id.btnRegresar).setOnClickListener {
            finish()
        }
    }
}