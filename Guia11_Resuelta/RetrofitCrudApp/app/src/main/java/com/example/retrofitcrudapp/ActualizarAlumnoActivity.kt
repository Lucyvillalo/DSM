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

class ActualizarAlumnoActivity : AppCompatActivity() {
    private var solicitud: Call<Alumno>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actualizar_alumno)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        val nombre = findViewById<EditText>(R.id.nombreEditText)
        val apellido = findViewById<EditText>(R.id.apellidoEditText)
        val edad = findViewById<EditText>(R.id.edadEditText)
        val guardar = findViewById<Button>(R.id.actualizarButton)
        val id = intent.getIntExtra("alumno_id", -1)
        if (id < 0) { Toast.makeText(this, "ID invÃ¡lido", Toast.LENGTH_SHORT).show(); finish(); return }
        nombre.setText(intent.getStringExtra("nombre"))
        apellido.setText(intent.getStringExtra("apellido"))
        edad.setText(intent.getIntExtra("edad", -1).takeIf { it >= 0 }?.toString().orEmpty())
        guardar.setOnClickListener {
            if (!validarFormulario(nombre, apellido, edad)) return@setOnClickListener
            val dato = Alumno(id, nombre.text.toString().trim(), apellido.text.toString().trim(), edad.text.toString().toInt())
            guardar.isEnabled = false
            solicitud = RetrofitClient.instance.actualizarAlumno(id, dato)
            solicitud!!.enqueue(object : Callback<Alumno> {
                override fun onResponse(call: Call<Alumno>, response: Response<Alumno>) {
                    guardar.isEnabled = true
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@ActualizarAlumnoActivity, "Alumno actualizado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Log.e("API", "HTTP ${response.code()}: ${response.errorBody()?.string()}")
                        Toast.makeText(this@ActualizarAlumnoActivity, "Error al actualizar el alumno (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Alumno>, t: Throwable) {
                    if (call.isCanceled) return
                    guardar.isEnabled = true
                    Log.e("API", "Error de conexiÃ³n", t)
                    Toast.makeText(this@ActualizarAlumnoActivity, "Error de conexiÃ³n al actualizar el alumno", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
    override fun onDestroy() { solicitud?.cancel(); super.onDestroy() }
}
