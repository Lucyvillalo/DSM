package com.example.retrofitcrudapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearProfesorActivity : AppCompatActivity() {
    private var solicitud: Call<Profesor>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_profesor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        val nombre = findViewById<EditText>(R.id.editTextNombre)
        val apellido = findViewById<EditText>(R.id.editTextApellido)
        val edad = findViewById<EditText>(R.id.editTextEdad)
        val guardar = findViewById<Button>(R.id.btnGuardar)
        
        guardar.setOnClickListener {
            if (!validarFormulario(nombre, apellido, edad)) return@setOnClickListener
            val dato = Profesor(0, nombre.text.toString().trim(), apellido.text.toString().trim(), edad.text.toString().toInt())
            guardar.isEnabled = false
            solicitud = RetrofitClient.profesorInstance.crearProfesor(dato)
            solicitud!!.enqueue(object : Callback<Profesor> {
                override fun onResponse(call: Call<Profesor>, response: Response<Profesor>) {
                    guardar.isEnabled = true
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@CrearProfesorActivity, "Profesor creado exitosamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Log.e("API", "HTTP ${response.code()}: ${response.errorBody()?.string()}")
                        Toast.makeText(this@CrearProfesorActivity, "Error al crear el profesor (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Profesor>, t: Throwable) {
                    if (call.isCanceled) return
                    guardar.isEnabled = true
                    Log.e("API", "Error de conexión", t)
                    Toast.makeText(this@CrearProfesorActivity, "Error de conexión al crear el profesor", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
    override fun onDestroy() { solicitud?.cancel(); super.onDestroy() }
}
