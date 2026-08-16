package com.example.guia5

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_muestra, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.opcion1 -> {
                startActivity(Intent(this, Activity1::class.java))
                true
            }
            R.id.opcion2 -> {
                startActivity(Intent(this, Activity2::class.java))
                true
            }
            R.id.opcion3 -> {
                startActivity(Intent(this, Activity3::class.java))
                true
            }
            R.id.opcion4 -> {
                startActivity(Intent(this, Activity4::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}