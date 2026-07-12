package com.example.ejercicio2guia2
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    lateinit var etNumero1: EditText
    lateinit var etNumero2: EditText

    lateinit var btnSuma: Button
    lateinit var btnResta: Button
    lateinit var btnMultiplicacion: Button
    lateinit var btnDivision: Button
    lateinit var btnPorcentaje: Button
    lateinit var btnCuadrado: Button
    lateinit var btnRaiz: Button
    lateinit var btnLimpiarTodo: Button

    lateinit var tvResultado: TextView
    lateinit var tvHistorial: TextView

    private val historial = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etNumero1 = findViewById(R.id.etNumero1)
        etNumero2 = findViewById(R.id.etNumero2)

        btnSuma = findViewById(R.id.btnSuma)
        btnResta = findViewById(R.id.btnResta)
        btnMultiplicacion = findViewById(R.id.btnMultiplicacion)
        btnDivision = findViewById(R.id.btnDivision)
        btnPorcentaje = findViewById(R.id.btnPorcentaje)
        btnCuadrado = findViewById(R.id.btnCuadrado)
        btnRaiz = findViewById(R.id.btnRaiz)
        btnLimpiarTodo = findViewById(R.id.btnLimpiarTodo)

        tvResultado = findViewById(R.id.tvResultado)
        tvHistorial = findViewById(R.id.tvHistorial)

        btnSuma.setOnClickListener {
            operacionBinaria { n1, n2 -> n1 + n2 }.let { (n1, n2, resultado) ->
                if (resultado != null) registrarOperacion(n1!!, "+", n2!!, resultado)
            }
        }

        btnResta.setOnClickListener {
            operacionBinaria { n1, n2 -> n1 - n2 }.let { (n1, n2, resultado) ->
                if (resultado != null) registrarOperacion(n1!!, "-", n2!!, resultado)
            }
        }

        btnMultiplicacion.setOnClickListener {
            operacionBinaria { n1, n2 -> n1 * n2 }.let { (n1, n2, resultado) ->
                if (resultado != null) registrarOperacion(n1!!, "x", n2!!, resultado)
            }
        }

        btnDivision.setOnClickListener {
            val n1 = obtenerNumero(etNumero1)
            val n2 = obtenerNumero(etNumero2)
            if (n1 == null || n2 == null) {
                mostrarToast("Ingresa Número 1 y Número 2 válidos.")
            } else if (n2 == 0.0) {
                mostrarToast("Error: no se puede dividir entre cero.")
            } else {
                registrarOperacion(n1, "÷", n2, n1 / n2)
            }
        }

        btnPorcentaje.setOnClickListener {
            val n1 = obtenerNumero(etNumero1)
            if (n1 == null) {
                mostrarToast("Ingresa un Número 1 válido.")
            } else {
                registrarOperacionUnaria(n1, "%", n1 / 100)
            }
        }

        btnCuadrado.setOnClickListener {
            val n1 = obtenerNumero(etNumero1)
            if (n1 == null) {
                mostrarToast("Ingresa un Número 1 válido.")
            } else {
                registrarOperacionUnaria(n1, "²", n1.pow(2))
            }
        }

        btnRaiz.setOnClickListener {
            val n1 = obtenerNumero(etNumero1)
            if (n1 == null) {
                mostrarToast("Ingresa un Número 1 válido.")
            } else if (n1 < 0) {
                mostrarToast("Error: no existe raíz cuadrada real de un número negativo.")
            } else {
                registrarOperacionUnaria(n1, "√", sqrt(n1))
            }
        }

        btnLimpiarTodo.setOnClickListener {
            etNumero1.text.clear()
            etNumero2.text.clear()
            tvResultado.text = "0"
            historial.clear()
            tvHistorial.text = ""
            etNumero1.requestFocus()
            mostrarToast("Pantalla reiniciada")
        }
    }

    private fun operacionBinaria(operacion: (Double, Double) -> Double): Triple<Double?, Double?, Double?> {
        val n1 = obtenerNumero(etNumero1)
        val n2 = obtenerNumero(etNumero2)
        return if (n1 == null || n2 == null) {
            mostrarToast("Ingresa Número 1 y Número 2 válidos.")
            Triple(null, null, null)
        } else {
            Triple(n1, n2, operacion(n1, n2))
        }
    }

    private fun obtenerNumero(campo: EditText): Double? {
        return campo.text.toString().trim().toDoubleOrNull()
    }

    private fun registrarOperacion(n1: Double, simbolo: String, n2: Double, resultado: Double) {
        tvResultado.text = formatearNumero(resultado)
        val linea = "${formatearNumero(n1)} $simbolo ${formatearNumero(n2)} = ${formatearNumero(resultado)}"
        actualizarHistorial(linea)
    }

    private fun registrarOperacionUnaria(n1: Double, simbolo: String, resultado: Double) {
        tvResultado.text = formatearNumero(resultado)
        val linea = when (simbolo) {
            "%" -> "${formatearNumero(n1)}% = ${formatearNumero(resultado)}"
            "²" -> "${formatearNumero(n1)}² = ${formatearNumero(resultado)}"
            "√" -> "√${formatearNumero(n1)} = ${formatearNumero(resultado)}"
            else -> "${formatearNumero(n1)} $simbolo = ${formatearNumero(resultado)}"
        }
        actualizarHistorial(linea)
    }

    private fun actualizarHistorial(linea: String) {
        historial.add(0, linea)
        if (historial.size > 5) {
            historial.removeAt(historial.size - 1)
        }
        tvHistorial.text = historial.joinToString(separator = "\n")
    }

    private fun formatearNumero(valor: Double): String {
        return if (valor == valor.toLong().toDouble()) {
            valor.toLong().toString()
        } else {
            String.format("%.2f", valor)
        }
    }

    private fun mostrarToast(mensaje: String, duracion: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, mensaje, duracion).show()
    }
}