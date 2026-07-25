package com.example.guia4

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CamaraActivity : AppCompatActivity() {

    private val TAG = "Guia4-Permisos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camara)
        comprobarPermiso()
    }

    private val solicitarPermiso = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            Log.i(TAG, getString(R.string.permiso_camara_concedido))
            Log.i(TAG, "Abriendo camara...")
        } else {
            Log.i(TAG, getString(R.string.permiso_camara_denegado))
        }
    }

    private fun comprobarPermiso() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i(TAG, "Abriendo camara...")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                mostrarDialogo()
            }

            else -> {
                solicitarPermiso.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun mostrarDialogo() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.permiso_camara_requerido))
            .setTitle("Permission required")
        builder.setPositiveButton("OK") { _, _ ->
            Log.i(TAG, "Seleccionado")
            solicitarPermiso.launch(
                Manifest.permission.CAMERA
            )
        }
        builder.setNegativeButton("Cancelar", null)
        val dialog = builder.create()
        dialog.show()
    }
}