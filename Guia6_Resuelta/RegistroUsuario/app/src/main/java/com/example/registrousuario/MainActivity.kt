package com.example.registrousuario

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickLinear(v: View?) {
        startActivity(Intent(this, RegistroLinearActivity::class.java))
    }

    fun onClickFrame(v: View?) {
        startActivity(Intent(this, RegistroFrameActivity::class.java))
    }

    fun onClickTable(v: View?) {
        startActivity(Intent(this, RegistroTableActivity::class.java))
    }

    fun onClickConstraint(v: View?) {
        startActivity(Intent(this, RegistroConstraintActivity::class.java))
    }
}