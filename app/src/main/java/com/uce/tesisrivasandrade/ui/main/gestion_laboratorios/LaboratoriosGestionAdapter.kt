package com.uce.tesisrivasandrade.ui.main.gestion_laboratorios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO

class LaboratoriosGestionAdapter(
    private var laboratorios: List<LaboratorioResponseDTO>,
    private val isAdmin: Boolean = false,
    private val isPapelera: Boolean = false,
    private val onVerClick: (LaboratorioResponseDTO) -> Unit,
    private val onEditarClick: (LaboratorioResponseDTO) -> Unit = {},
    private val onEliminarClick: (LaboratorioResponseDTO) -> Unit = {}
) : RecyclerView.Adapter<LaboratoriosGestionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreLaboratorio)
        val tvUbicacion: TextView = view.findViewById(R.id.tvUbicacion)
        val tvCapacidad: TextView = view.findViewById(R.id.tvCapacidad)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val btnVer: View = view.findViewById(R.id.btnVer)
        val btnEditar: MaterialButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: MaterialButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_laboratorio_gestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lab = laboratorios[position]
        holder.tvNombre.text = lab.nombre
        holder.tvUbicacion.text = lab.ubicacion ?: "Sin ubicación"
        holder.tvCapacidad.text = "${lab.capacidad ?: 0} personas"
        
        if (isPapelera) {
            holder.tvEstado.text = "Eliminado"
            holder.tvEstado.setTextColor(android.graphics.Color.parseColor("#E74C3C"))
            holder.btnVer.visibility = View.GONE
            // Usamos un icono de restaurar si estamos en papelera
            holder.btnEditar.setIconResource(android.R.drawable.ic_menu_rotate)
        } else {
            holder.tvEstado.text = "Activo"
            holder.tvEstado.setTextColor(android.graphics.Color.parseColor("#27AE60"))
            holder.btnVer.visibility = View.VISIBLE
            holder.btnEditar.setIconResource(android.R.drawable.ic_menu_edit)
        }

        // Visibilidad según rol
        if (isAdmin) {
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnEliminar.visibility = View.VISIBLE
        } else {
            holder.btnEditar.visibility = View.GONE
            holder.btnEliminar.visibility = View.GONE
        }

        holder.btnVer.setOnClickListener { onVerClick(lab) }
        holder.btnEditar.setOnClickListener { onEditarClick(lab) }
        holder.btnEliminar.setOnClickListener { onEliminarClick(lab) }
    }

    override fun getItemCount() = laboratorios.size

    fun updateData(newData: List<LaboratorioResponseDTO>) {
        this.laboratorios = newData
        notifyDataSetChanged()
    }
}
