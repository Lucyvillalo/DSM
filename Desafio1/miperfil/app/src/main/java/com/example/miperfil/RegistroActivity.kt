package com.example.miperfil

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Activity 2: Registro de Perfil
 * Permite ingresar los datos personales, solicitar el permiso de cámara
 * en tiempo de ejecución y validar el formulario antes de continuar.
 */
class RegistroActivity : AppCompatActivity() {

    // Constante TAG para identificar los logs de esta Activity
    private val TAG = "MiPerfil-Permisos"

    // Referencias a los campos del formulario
    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etDireccion: EditText
    private lateinit var tvPermisoEstado: TextView
    private lateinit var btnTomarFoto: MaterialButton
    private lateinit var btnGuardar: MaterialButton
    private lateinit var bottomNav: BottomNavigationView

    // 3. Solicitud de permiso
    // Launcher moderno que reemplaza a onRequestPermissionsResult()
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // 4. Manejo de respuesta de solicitud de permiso
            if (isGranted) {
                Log.i(TAG, "Permiso de cámara concedido por el usuario")
                tvPermisoEstado.text = getString(R.string.permiso_camara_concedido)
                Toast.makeText(
                    this,
                    getString(R.string.permiso_camara_concedido),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.i(TAG, "Permiso de cámara denegado por el usuario")
                tvPermisoEstado.text = getString(R.string.permiso_camara_denegado)
                Toast.makeText(
                    this,
                    getString(R.string.permiso_camara_denegado),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        Log.i(TAG, "onCreate: Pantalla de registro de perfil iniciada")

        // Enlazar las vistas del layout
        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etTelefono = findViewById(R.id.etTelefono)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etDireccion = findViewById(R.id.etDireccion)
        tvPermisoEstado = findViewById(R.id.tvPermisoEstado)
        btnTomarFoto = findViewById(R.id.btnTomarFoto)
        btnGuardar = findViewById(R.id.btnGuardar)
        bottomNav = findViewById(R.id.bottomNavigation)

        // Listener del botón Tomar Foto: dispara el flujo de permisos
        btnTomarFoto.setOnClickListener {
            verificarYSolicitarPermisoCamara()
        }

        // Listener del botón Guardar: valida y navega a PerfilGuardadoActivity
        btnGuardar.setOnClickListener {
            validarYGuardarPerfil()
        }

        configurarNavegacionInferior()
    }

    /**
     * Configura la barra de navegación inferior (Inicio / Registro / Perfil).
     * "Registro" queda marcado como seleccionado porque es la pantalla actual.
     */
    private fun configurarNavegacionInferior() {
        bottomNav.selectedItemId = R.id.nav_registro

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_registro -> {
                    // Ya estamos en Registro, no se hace nada
                    true
                }
                R.id.nav_perfil -> {
                    Toast.makeText(
                        this,
                        getString(R.string.nav_perfil_sin_datos),
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Flujo completo de permisos en tiempo de ejecución para la cámara.
     * Sigue el patrón: comprobar -> rationale -> solicitar -> manejar respuesta
     */
    private fun verificarYSolicitarPermisoCamara() {

        // 1. Comprobar estado permiso
        val permisoConcedido = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (permisoConcedido) {
            Log.i(TAG, "El permiso de cámara ya estaba concedido")
            tvPermisoEstado.text = getString(R.string.permiso_camara_concedido)
            return
        }

        // 2. Configurar permiso
        // Se evalúa si el sistema recomienda mostrar una explicación previa
        val mostrarRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.CAMERA
        )

        if (mostrarRationale) {
            Log.i(TAG, "Mostrando rationale antes de solicitar el permiso")
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.rationale_titulo))
                .setMessage(getString(R.string.rationale_mensaje))
                .setPositiveButton(getString(R.string.rationale_aceptar)) { dialog, _ ->
                    dialog.dismiss()
                    // 3. Solicitud de permiso
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                .setNegativeButton(getString(R.string.rationale_cancelar)) { dialog, _ ->
                    dialog.dismiss()
                    Log.i(TAG, "Usuario canceló el diálogo de rationale")
                }
                .show()
        } else {
            // 3. Solicitud de permiso
            Log.i(TAG, "Solicitando permiso de cámara directamente")
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /**
     * Valida todos los campos del formulario. Si son correctos, arma el Intent
     * con los datos y navega hacia PerfilGuardadoActivity.
     */
    private fun validarYGuardarPerfil() {

        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val fechaNacimiento = etFechaNacimiento.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()

        // 1. Validar campos vacíos
        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() ||
            fechaNacimiento.isEmpty() || direccion.isEmpty()
        ) {
            Log.i(TAG, "Validación fallida: hay campos vacíos")
            mostrarToastPersonalizado(getString(R.string.error_campos_vacios))
            return
        }

        // 2. Validar formato de correo electrónico
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Log.i(TAG, "Validación fallida: correo con formato inválido")
            mostrarToastPersonalizado(getString(R.string.error_correo_invalido))
            return
        }

        // 3. Validar formato de teléfono: números de El Salvador tienen 8 dígitos,
        // con o sin el código de país +503. Se quitan espacios antes de validar
        // para aceptar tanto "7000 0000" como "70000000" o "+503 7000 0000".
        val telefonoSinEspacios = telefono.replace(" ", "")
        val telefonoRegex = Regex("^(\\+503|503)?[0-9]{8}$")
        if (!telefonoRegex.matches(telefonoSinEspacios)) {
            Log.i(TAG, "Validación fallida: teléfono con formato inválido")
            mostrarToastPersonalizado(getString(R.string.error_telefono_invalido))
            return
        }

        // 4. Validar fecha de nacimiento: formato dd/mm/aaaa Y que sea una fecha real del calendario
        if (!esFechaValida(fechaNacimiento)) {
            Log.i(TAG, "Validación fallida: fecha de nacimiento inválida o inexistente")
            mostrarToastPersonalizado(getString(R.string.error_fecha_invalida))
            return
        }

        // 5. Todo correcto: se arma el Intent con los datos y se navega
        Log.i(TAG, "Validación exitosa, enviando datos a PerfilGuardadoActivity")

        val intent = Intent(this, PerfilGuardadoActivity::class.java).apply {
            putExtra(EXTRA_NOMBRE, nombre)
            putExtra(EXTRA_CORREO, correo)
            putExtra(EXTRA_TELEFONO, telefono)
            putExtra(EXTRA_FECHA_NACIMIENTO, fechaNacimiento)
            putExtra(EXTRA_DIRECCION, direccion)
        }
        startActivity(intent)
    }

    /**
     * Muestra un Toast con una duración personalizada y exacta.
     * La API estándar de Toast solo ofrece dos duraciones fijas:
     * LENGTH_SHORT (~2 seg) y LENGTH_LONG (~3.5 seg), así que para lograr
     * exactamente 4 segundos se reutiliza el mismo Toast, volviéndolo a
     * mostrar cada segundo con un CountDownTimer hasta cumplir el tiempo total.
     */
    private fun mostrarToastPersonalizado(mensaje: String, duracionMs: Long = 4000) {
        val toast = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT)
        toast.show()

        object : CountDownTimer(duracionMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                toast.show()
            }

            override fun onFinish() {
                toast.cancel()
            }
        }.start()
    }

    /**
     * Valida que la fecha tenga el formato dd/mm/aaaa Y que corresponda a una
     * fecha real del calendario (por ejemplo, rechaza 31/02/2026).
     * Se usa SimpleDateFormat en modo estricto (setLenient(false)) para que
     * Java no "corrija" fechas inválidas automáticamente.
     */
    private fun esFechaValida(fecha: String): Boolean {
        // Primero se valida el formato con una expresión regular simple
        val formatoRegex = Regex("^\\d{2}/\\d{2}/\\d{4}$")
        if (!formatoRegex.matches(fecha)) {
            return false
        }

        // Luego se valida que la fecha exista realmente en el calendario
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formato.isLenient = false

        return try {
            formato.parse(fecha)
            true
        } catch (e: ParseException) {
            false
        }
    }

    companion object {
        // Claves usadas para enviar los datos mediante el Intent
        const val EXTRA_NOMBRE = "extra_nombre"
        const val EXTRA_CORREO = "extra_correo"
        const val EXTRA_TELEFONO = "extra_telefono"
        const val EXTRA_FECHA_NACIMIENTO = "extra_fecha_nacimiento"
        const val EXTRA_DIRECCION = "extra_direccion"
    }
}
