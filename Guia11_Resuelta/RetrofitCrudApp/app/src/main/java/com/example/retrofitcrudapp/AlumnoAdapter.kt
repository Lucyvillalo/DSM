package com.example.retrofitcrudapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlumnoAdapter(private val datos: List<Alumno>) : RecyclerView.Adapter<AlumnoAdapter.ViewHolder>() {
    private var onItemClick: OnItemClickListener? = null
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.tvNombre)
        val apellidoTextView: TextView = view.findViewById(R.id.tvApellido)
        val edadTextView: TextView = view.findViewById(R.id.tvEdad)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_alumno, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dato = datos[position]
        holder.nombreTextView.text = dato.nombre
        holder.apellidoTextView.text = dato.apellido
        holder.edadTextView.text = dato.edad?.toString() ?: "Edad no disponible"
        holder.itemView.setOnClickListener { onItemClick?.onItemClick(dato) }
    }
    override fun getItemCount() = datos.size
    fun setOnItemClickListener(listener: OnItemClickListener) { onItemClick = listener }
    interface OnItemClickListener { fun onItemClick(alumno: Alumno) }
}
