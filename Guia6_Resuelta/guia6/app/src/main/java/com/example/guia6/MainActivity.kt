package com.example.guia6

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) {
                v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    fun onClickFrame(v: View?) {
        val llamar = Intent(this, FrameLayout::class.java)
        startActivity(llamar)
    }

    fun onClickLinear(v: View?) {
        val llamar = Intent(this, LinearLayout::class.java)
        startActivity(llamar)
    }

    fun onClickRelative(v: View?) {
        val llamar = Intent(this, RelativeLayout::class.java)
        startActivity(llamar)
    }

    fun onClickTable(v: View?) {
        val llamar = Intent(this, TableLayout::class.java)
        startActivity(llamar)
    }

    fun onClickContraint(v: View?) {
        val llamar = Intent(this, ConstraintLayout::class.java)
        startActivity(llamar)
    }

    fun onClickGrid(v: View?) {
        val llamar = Intent(this, GridLayout::class.java)
        startActivity(llamar)
    }
}