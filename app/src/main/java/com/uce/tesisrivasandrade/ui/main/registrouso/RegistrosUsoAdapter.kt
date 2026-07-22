package com.uce.tesisrivasandrade.ui.main.registrouso

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.remote.registrouso.RegistroUsoResponseDTO

class RegistrosUsoAdapter : ListAdapter<RegistroUsoResponseDTO, RegistrosUsoAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLaboratorio: TextView = view.findViewById(R.id.txtLaboratorio)
        val tvUsuario: TextView = view.findViewById(R.id.txtUsuario)
        val tvEntrada: TextView = view.findViewById(R.id.txtEntrada)
        val tvSalida: TextView = view.findViewById(R.id.txtSalida)
        val tvDuracion: TextView = view.findViewById(R.id.txtDuracion)
        val tvEstado: TextView = view.findViewById(R.id.txtEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro_uso, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvLaboratorio.text = item.laboratorioNombre
        holder.tvUsuario.text = item.usuarioNombre
        holder.tvEntrada.text = "Entrada: ${formatDate(item.fechaEntrada)}"
        holder.tvSalida.text = "Salida: ${item.fechaSalida?.let { formatDate(it) } ?: "-"}"
        holder.tvDuracion.text = "Duración: ${formatearDuracion(item.duracionMinutos)}"

        if (item.activo) {
            holder.tvEstado.text = "ACTIVO"
            holder.tvEstado.setBackgroundResource(R.drawable.bg_status_active)
        } else {
            holder.tvEstado.text = "CERRADO"
            holder.tvEstado.setBackgroundResource(R.drawable.bg_status_closed)
        }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            if (dateStr.contains("T")) {
                val cleanDate = dateStr.split(".")[0]
                cleanDate.replace("T", " ").substring(0, 16)
            } else {
                dateStr
            }
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun formatearDuracion(minutos: Long?): String {
        if (minutos == null || minutos < 0) return "-"
        if (minutos == 0L) return "0 min"
        val horas = minutos / 60
        val mins = minutos % 60
        return when {
            horas > 0 && mins > 0 -> "${horas}h ${mins}min"
            horas > 0 -> "${horas}h"
            else -> "${mins}min"
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<RegistroUsoResponseDTO>() {
        override fun areItemsTheSame(oldItem: RegistroUsoResponseDTO, newItem: RegistroUsoResponseDTO): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RegistroUsoResponseDTO, newItem: RegistroUsoResponseDTO): Boolean {
            return oldItem == newItem
        }
    }
}
