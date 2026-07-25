package com.example.miperfil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

/**
 * Activity 3: Perfil Guardado
 * Muestra el resumen del perfil ingresado y permite volver al inicio
 * o registrar un nuevo perfil.
 */
class PerfilGuardadoActivity : AppCompatActivity() {

    // Constante TAG para identificar los logs de esta Activity
    private val TAG = "MiPerfil-Resumen"

    private lateinit var tvNombre: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvFechaNacimiento: TextView
    private lateinit var tvDireccion: TextView
    private lateinit var btnRegresarInicio: MaterialButton
    private lateinit var btnNuevoPerfil: MaterialButton
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_guardado)

        Log.i(TAG, "onCreate: Pantalla de perfil guardado iniciada")

        // Enlazar las vistas del layout
        tvNombre = findViewById(R.id.tvNombre)
        tvCorreo = findViewById(R.id.tvCorreo)
        tvTelefono = findViewById(R.id.tvTelefono)
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento)
        tvDireccion = findViewById(R.id.tvDireccion)
        btnRegresarInicio = findViewById(R.id.btnRegresarInicio)
        btnNuevoPerfil = findViewById(R.id.btnNuevoPerfil)
        bottomNav = findViewById(R.id.bottomNavigation)

        // 1. Recibir los datos enviados desde RegistroActivity
        mostrarDatosRecibidos()

        // 2. Listener del botón Regresar al inicio
        btnRegresarInicio.setOnClickListener {
            regresarAlInicio()
        }

        // 3. Listener del botón Nuevo perfil
        btnNuevoPerfil.setOnClickListener {
            irANuevoPerfil()
        }

        configurarNavegacionInferior()
    }

    /**
     * Configura la barra de navegación inferior (Inicio / Registro / Perfil).
     * "Perfil" queda marcado como seleccionado porque es la pantalla actual.
     */
    private fun configurarNavegacionInferior() {
        bottomNav.selectedItemId = R.id.nav_perfil

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    regresarAlInicio()
                    true
                }
                R.id.nav_registro -> {
                    irANuevoPerfil()
                    true
                }
                R.id.nav_perfil -> {
                    // Ya estamos en Perfil, no se hace nada
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Navega hacia MainActivity limpiando por completo el stack de Activities,
     * para que el usuario no pueda volver hacia atrás con el botón físico.
     */
    private fun regresarAlInicio() {
        Log.i(TAG, "Regresando a la pantalla de bienvenida, limpiando el stack")

        // 4. Intent explícito a MainActivity limpiando el stack de Activities
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Navega hacia RegistroActivity para permitir el ingreso de un nuevo perfil.
     */
    private fun irANuevoPerfil() {
        Log.i(TAG, "Navegando a RegistroActivity para un nuevo perfil")

        // 5. Intent explícito hacia RegistroActivity
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Extrae los datos del Intent recibido y los muestra en los TextView
     * correspondientes de la pantalla resumen.
     */
    private fun mostrarDatosRecibidos() {
        val nombre = intent.getStringExtra(RegistroActivity.EXTRA_NOMBRE) ?: ""
        val correo = intent.getStringExtra(RegistroActivity.EXTRA_CORREO) ?: ""
        val telefono = intent.getStringExtra(RegistroActivity.EXTRA_TELEFONO) ?: ""
        val fechaNacimiento = intent.getStringExtra(RegistroActivity.EXTRA_FECHA_NACIMIENTO) ?: ""
        val direccion = intent.getStringExtra(RegistroActivity.EXTRA_DIRECCION) ?: ""

        Log.i(TAG, "Datos recibidos correctamente, actualizando la interfaz")

        tvNombre.text = nombre
        tvCorreo.text = correo
        tvTelefono.text = telefono
        tvFechaNacimiento.text = fechaNacimiento
        tvDireccion.text = direccion
    }
}
