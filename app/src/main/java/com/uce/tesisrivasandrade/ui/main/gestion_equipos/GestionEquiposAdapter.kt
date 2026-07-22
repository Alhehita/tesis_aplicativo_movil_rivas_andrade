package com.uce.tesisrivasandrade.ui.main.gestion_equipos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse

class GestionEquiposAdapter(
    private val isAdmin: Boolean = false,
    private val onVerClick: (GestionEquiposResponse) -> Unit,
    private val onEditarClick: (GestionEquiposResponse) -> Unit = {},
    private val onEliminarClick: (GestionEquiposResponse) -> Unit = {}
) : ListAdapter<GestionEquiposResponse, GestionEquiposAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreEquipo)
        val tvCodigo: TextView = view.findViewById(R.id.tvCodigo)
        val tvTipo: TextView = view.findViewById(R.id.tvTipo)
        val tvMarca: TextView = view.findViewById(R.id.tvMarca)
        val tvModelo: TextView = view.findViewById(R.id.tvModelo)
        val tvSerie: TextView = view.findViewById(R.id.tvSerie)
        val tvLaboratorio: TextView = view.findViewById(R.id.tvLaboratorio)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)

        val btnVer: View = view.findViewById(R.id.btnVer)
        val btnEditar: View = view.findViewById(R.id.btnEditar)
        val btnEliminar: View = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipo_gestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val equipo = getItem(position)

        holder.tvNombre.text = "Equipo: ${equipo.codigo}"
        holder.tvCodigo.text = equipo.codigo
        holder.tvTipo.text = equipo.tipo
        holder.tvMarca.text = equipo.marca ?: "N/A"
        holder.tvModelo.text = equipo.modelo ?: "N/A"
        holder.tvSerie.text = equipo.numeroSerie ?: "N/A"
        holder.tvLaboratorio.text = equipo.laboratorioNombre ?: "Sin asignar"
        holder.tvEstado.text = equipo.estado

        val colorEstado = when(equipo.estado.uppercase()) {
            "OPERATIVO" -> "#27AE60"
            "DAÑADO" -> "#E74C3C"
            "MANTENIMIENTO" -> "#F39C12"
            else -> "#7F8C8D"
        }
        holder.tvEstado.setTextColor(android.graphics.Color.parseColor(colorEstado))

        if (isAdmin) {
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnEliminar.visibility = View.VISIBLE
        } else {
            holder.btnEditar.visibility = View.GONE
            holder.btnEliminar.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onVerClick(equipo) }
        holder.btnVer.setOnClickListener { onVerClick(equipo) }
        holder.btnEditar.setOnClickListener { onEditarClick(equipo) }
        holder.btnEliminar.setOnClickListener { onEliminarClick(equipo) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GestionEquiposResponse>() {
        override fun areItemsTheSame(oldItem: GestionEquiposResponse, newItem: GestionEquiposResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GestionEquiposResponse, newItem: GestionEquiposResponse): Boolean {
            return oldItem == newItem
        }
    }
}
