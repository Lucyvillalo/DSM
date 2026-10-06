package com.example.retrofitcrudapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val api = RetrofitClient.instance
    private var consulta: Call<List<Alumno>>? = null
    private var eliminacion: Call<Void>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<Button>(R.id.btnReintentar).setOnClickListener { cargarDatos() }
        findViewById<FloatingActionButton>(R.id.fab_agregar).setOnClickListener {
            startActivity(Intent(this, CrearAlumnoActivity::class.java))
        }
        findViewById<Button>(R.id.btnCambiar).setOnClickListener {
            startActivity(Intent(this, ProfesorActivity::class.java))
        }
    }
    override fun onResume() { super.onResume(); cargarDatos() }
    private fun cargarDatos() {
        consulta?.cancel()
        mostrarEstado(cargando = true)
        consulta = api.obtenerAlumnos()
        consulta!!.enqueue(object : Callback<List<Alumno>> {
            override fun onResponse(call: Call<List<Alumno>>, response: Response<List<Alumno>>) {
                if (response.isSuccessful && response.body() != null) {
                    val datos = response.body()!!
                    val adapter = AlumnoAdapter(datos)
                    recyclerView.adapter = adapter
                    mostrarEstado(if (datos.isEmpty()) "No hay alumnos registrados. Pulsa + para agregar uno." else null)
                    adapter.setOnItemClickListener(object : AlumnoAdapter.OnItemClickListener {
                        override fun onItemClick(alumno: Alumno) {
                            AlertDialog.Builder(this@MainActivity).setTitle(alumno.nombre)
                                .setItems(arrayOf("Modificar Alumno", "Eliminar Alumno")) { _, index ->
                                    if (index == 0) modificar(alumno) else confirmarEliminacion(alumno)
                                }.setNegativeButton("Cancelar", null).show()
                        }
                    })
                } else {
                    Log.e("API", "HTTP ${response.code()}: ${response.errorBody()?.string()}")
                    mostrarEstado("No se pudieron cargar los alumnos (HTTP ${response.code()}). Intenta de nuevo.")
                }
            }
            override fun onFailure(call: Call<List<Alumno>>, t: Throwable) {
                if (call.isCanceled) return
                Log.e("API", "Error de conexiÃ³n", t)
                val mensaje = if (t is com.google.gson.JsonParseException) "La API devuelve datos invÃ¡lidos: revise que id y edad sean numÃ©ricos en MockAPI" else "Error de conexiÃ³n al obtener alumnos"
                mostrarEstado(mensaje)
            }
        })
    }
    private fun mostrarEstado(mensaje: String? = null, cargando: Boolean = false) {
        recyclerView.visibility = if (mensaje == null && !cargando) View.VISIBLE else View.GONE
        findViewById<ProgressBar>(R.id.progressCarga).visibility = if (cargando) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.tvEstado).apply {
            text = mensaje.orEmpty()
            visibility = if (mensaje != null) View.VISIBLE else View.GONE
        }
        findViewById<Button>(R.id.btnReintentar).visibility = if (mensaje != null) View.VISIBLE else View.GONE
    }
    private fun modificar(dato: Alumno) {
        startActivity(Intent(this, ActualizarAlumnoActivity::class.java).apply {
            putExtra("alumno_id", dato.id)
            putExtra("nombre", dato.nombre)
            putExtra("apellido", dato.apellido)
            putExtra("edad", dato.edad ?: -1)
        })
    }
    private fun confirmarEliminacion(dato: Alumno) {
        AlertDialog.Builder(this).setTitle("Eliminar alumno")
            .setMessage("¿Eliminar a ${dato.nombre} ${dato.apellido}?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarAlumno(dato) }
            .setNegativeButton("Cancelar", null).show()
    }
    private fun eliminarAlumno(dato: Alumno) {
        if (eliminacion != null) return
        eliminacion = api.eliminarAlumno(dato.id)
        eliminacion!!.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                eliminacion = null
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Alumno eliminado", Toast.LENGTH_SHORT).show()
                    cargarDatos()
                } else {
                    Log.e("API", "HTTP ${response.code()}: ${response.errorBody()?.string()}")
                    Toast.makeText(this@MainActivity, "Error al eliminar alumno (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                eliminacion = null
                if (!call.isCanceled) Toast.makeText(this@MainActivity, "Error de conexiÃ³n al eliminar alumno", Toast.LENGTH_LONG).show()
            }
        })
    }
    override fun onDestroy() { consulta?.cancel(); eliminacion?.cancel(); super.onDestroy() }
}
