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

class CrearAlumnoActivity : AppCompatActivity() {
    private var solicitud: Call<Alumno>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_alumno)
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
            val dato = Alumno(0, nombre.text.toString().trim(), apellido.text.toString().trim(), edad.text.toString().toInt())
            guardar.isEnabled = false
            solicitud = RetrofitClient.instance.crearAlumno(dato)
            solicitud!!.enqueue(object : Callback<Alumno> {
                override fun onResponse(call: Call<Alumno>, response: Response<Alumno>) {
                    guardar.isEnabled = true
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@CrearAlumnoActivity, "Alumno creado exitosamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Log.e("API", "HTTP ${response.code()}: ${response.errorBody()?.string()}")
                        Toast.makeText(this@CrearAlumnoActivity, "Error al crear el alumno (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Alumno>, t: Throwable) {
                    if (call.isCanceled) return
                    guardar.isEnabled = true
                    Log.e("API", "Error de conexión", t)
                    Toast.makeText(this@CrearAlumnoActivity, "Error de conexión al crear el alumno", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
    override fun onDestroy() { solicitud?.cancel(); super.onDestroy() }
}
