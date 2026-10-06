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

class ProfesorActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val api = RetrofitClient.profesorInstance
    private var consulta: Call<List<Profesor>>? = null
    private var eliminacion: Call<Void>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<Button>(R.id.btnReintentar).setOnClickListener { cargarDatos() }
        findViewById<FloatingActionButton>(R.id.fab_agregar).setOnClickListener {
            startActivity(Intent(this, CrearProfesorActivity::class.java))
        }
        findViewById<Button>(R.id.btnCambiar).setOnClickListener {
            finish()
        }
    }
    override fun onResume() { super.onResume(); cargarDatos() }
    private fun cargarDatos() {
        consulta?.cancel()
        mostrarEstado(cargando = true)
        consulta = api.obtenerProfesores()
        consulta!!.enqueue(object : Callback<List<Profesor>> {
            override fun onResponse(call: Call<List<Profesor>>, response: Response<List<Profesor>>) {
                if (response.isSuccessful && response.body() != null) {
                    val datos = response.body()!!
                    val adapter = ProfesorAdapter(datos)
                    recyclerView.adapter = adapter
                    mostrarEstado(if (datos.isEmpty()) "No hay profesores registrados. Pulsa + para agregar uno." else null)
                    adapter.setOnItemClickListener(object : ProfesorAdapter.OnItemClickListener {
                        override fun onItemClick(profesor: Profesor) {
                            AlertDialog.Builder(this@ProfesorActivity).setTitle(profesor.nombre)
                                .setItems(arrayOf("Modificar Profesor", "Eliminar Profesor")) { _, index ->
                                    if (index == 0) modificar(profesor) else confirmarEliminacion(profesor)
                                }.setNegativeButton("Cancelar", null).show()
                        }
                    })
                } else {
                    Log.e("API", "HTTP ${response.code()}: ${response.errorBody()?.string()}")
                    mostrarEstado("No se pudieron cargar los profesores (HTTP ${response.code()}). Intenta de nuevo.")
                }
            }
            override fun onFailure(call: Call<List<Profesor>>, t: Throwable) {
                if (call.isCanceled) return
                Log.e("API", "Error de conexiÃ³n", t)
                val mensaje = if (t is com.google.gson.JsonParseException) "La API devuelve datos invÃ¡lidos: revise que id y edad sean numÃ©ricos en MockAPI" else "Error de conexiÃ³n al obtener profesores"
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
    private fun modificar(dato: Profesor) {
        startActivity(Intent(this, ActualizarProfesorActivity::class.java).apply {
            putExtra("profesor_id", dato.id)
            putExtra("nombre", dato.nombre)
            putExtra("apellido", dato.apellido)
            putExtra("edad", dato.edad ?: -1)
        })
    }
    private fun confirmarEliminacion(dato: Profesor) {
        AlertDialog.Builder(this).setTitle("Eliminar profesor")
            .setMessage("¿Eliminar a ${dato.nombre} ${dato.apellido}?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarProfesor(dato) }
            .setNegativeButton("Cancelar", null).show()
    }
    private fun eliminarProfesor(dato: Profesor) {
        if (eliminacion != null) return
        eliminacion = api.eliminarProfesor(dato.id)
        eliminacion!!.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                eliminacion = null
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfesorActivity, "Profesor eliminado", Toast.LENGTH_SHORT).show()
                    cargarDatos()
                } else {
                    Log.e("API", "HTTP ${response.code()}: ${response.errorBody()?.string()}")
                    Toast.makeText(this@ProfesorActivity, "Error al eliminar profesor (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                eliminacion = null
                if (!call.isCanceled) Toast.makeText(this@ProfesorActivity, "Error de conexiÃ³n al eliminar profesor", Toast.LENGTH_LONG).show()
            }
        })
    }
    override fun onDestroy() { consulta?.cancel(); eliminacion?.cancel(); super.onDestroy() }
}
