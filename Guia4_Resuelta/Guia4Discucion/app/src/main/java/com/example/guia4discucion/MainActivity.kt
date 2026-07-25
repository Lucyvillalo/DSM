package com.example.guia4discucion

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val TAG = "Guia4-Permisos"
    private val CODIGO_SOLICITUD_GRABAR = 101

    private lateinit var btnIniciarGrabacion: Button
    private lateinit var tvEstado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnIniciarGrabacion = findViewById(R.id.btnIniciarGrabacion)
        tvEstado = findViewById(R.id.tvEstado)

        // 1. Comprobar el estado del permiso al iniciar
        actualizarEstadoInicial()

        btnIniciarGrabacion.setOnClickListener {
            manejarClickGrabacion()
        }
    }

    // 1. Comprobación inicial del permiso
    private fun actualizarEstadoInicial() {
        if (permisoConcedido()) {
            btnIniciarGrabacion.isEnabled = true
            tvEstado.text = getString(R.string.estado_esperando)
        } else {
            btnIniciarGrabacion.isEnabled = true
            tvEstado.text = getString(R.string.estado_esperando)
            solicitarPermiso()
        }
    }

    private fun permisoConcedido(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 2. Click en "Iniciar Grabación"
    private fun manejarClickGrabacion() {
        if (permisoConcedido()) {
            tvEstado.text = getString(R.string.estado_grabando)
            Log.i(TAG, "Grabando audio...")
        } else {
            solicitarPermiso()
        }
    }

    private fun solicitarPermiso() {
        val mostrarRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.RECORD_AUDIO
        )

        if (mostrarRationale) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permiso requerido")
                .setMessage(getString(R.string.permiso_audio_requerido))
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        CODIGO_SOLICITUD_GRABAR
                    )
                }
                .setNegativeButton("Cancelar", null)
                .create()
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                CODIGO_SOLICITUD_GRABAR
            )
        }
    }

    // 3. Resultado de la solicitud del permiso
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CODIGO_SOLICITUD_GRABAR) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permiso concedido por el usuario")
                Toast.makeText(this, getString(R.string.permiso_audio_concedido_usuario), Toast.LENGTH_SHORT).show()
                btnIniciarGrabacion.isEnabled = true
                tvEstado.text = getString(R.string.estado_esperando)
            } else {
                Log.i(TAG, "Permiso denegado por el usuario")
                Toast.makeText(this, getString(R.string.permiso_audio_denegado_usuario), Toast.LENGTH_SHORT).show()
                btnIniciarGrabacion.isEnabled = false
                tvEstado.text = getString(R.string.estado_denegado)
            }
        }
    }
}