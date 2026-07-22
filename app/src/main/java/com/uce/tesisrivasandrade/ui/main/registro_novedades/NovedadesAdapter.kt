package com.uce.tesisrivasandrade.ui.main.registro_novedades

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uce.tesisrivasandrade.data.model.registro_novedades.RegistroNovedadesResponse
import com.uce.tesisrivasandrade.databinding.ItemNovedadBinding
import com.uce.tesisrivasandrade.utils.DateUtils

class NovedadesAdapter(private val onItemClick: (RegistroNovedadesResponse) -> Unit) :
    ListAdapter<RegistroNovedadesResponse, NovedadesAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemNovedadBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(novedad: RegistroNovedadesResponse, onItemClick: (RegistroNovedadesResponse) -> Unit) {
            binding.tvTitulo.text = novedad.titulo
            binding.tvDescripcion.text = novedad.descripcion

            val nombreLab = novedad.laboratorio?.nombre ?: novedad.laboratorioNombre ?: "Sin laboratorio"
            binding.tvLaboratorio.text = nombreLab

            // Mostrar el código del equipo si existe
            val codigoEquipo = novedad.equipo?.codigo ?: novedad.equipoCodigo
            if (!codigoEquipo.isNullOrBlank()) {
                binding.tvEquipo.visibility = View.VISIBLE
                binding.tvEquipo.text = " • $codigoEquipo"
            } else {
                binding.tvEquipo.visibility = View.GONE
            }

            binding.tvFecha.text =
                DateUtils.formatFechaISO(novedad.fechaReporte ?: "")

            binding.chipTipo.text = novedad.tipo ?: "N/A"
            binding.chipEstado.text = novedad.estado ?: "N/A"
            binding.chipPrioridad.text = novedad.prioridad ?: "N/A"

            binding.ivHasImage.visibility =
                if (!novedad.imagenes.isNullOrEmpty()) View.VISIBLE else View.GONE

            // Configurar clics tanto en la tarjeta como en el botón "Ver"
            binding.root.setOnClickListener { onItemClick(novedad) }
            binding.btnVer.setOnClickListener { onItemClick(novedad) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNovedadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<RegistroNovedadesResponse>() {
        override fun areItemsTheSame(oldItem: RegistroNovedadesResponse, newItem: RegistroNovedadesResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RegistroNovedadesResponse, newItem: RegistroNovedadesResponse): Boolean {
            return oldItem == newItem
        }
    }
}
