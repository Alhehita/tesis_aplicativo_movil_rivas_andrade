package com.uce.tesisrivasandrade.ui.main.gestion_equipos

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse
import kotlinx.coroutines.launch

class DetalleEquipoFragment : Fragment(R.layout.fragment_detalle_equipo) {

    private var equipo: GestionEquiposResponse? = null
    private val viewModel: GestionEquiposViewModel by activityViewModels {
        GestionEquiposViewModel.factory(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener equipoId de argumentos de navegación
        val equipoId = arguments?.getLong("equipoId", -1L) ?: -1L

        // Buscar equipo en el ViewModel
        if (equipoId != -1L) {
            lifecycleScope.launch {
                viewModel.equipos.collect { lista ->
                    val encontrado = lista.find { it.id == equipoId }
                    if (encontrado != null) {
                        equipo = encontrado
                        actualizarUI(view)
                        return@collect
                    }
                }
            }
        }

        // La navegación hacia atrás usa el botón del toolbar (setupActionBarWithNavController)
    }

    private fun actualizarUI(view: View) {
        val tvCodigo = view.findViewById<TextView>(R.id.tvDetalleCodigo)
        val tvTipo = view.findViewById<TextView>(R.id.tvDetalleTipo)
        val tvLaboratorio = view.findViewById<TextView>(R.id.tvDetalleLaboratorio)
        val tvMarca = view.findViewById<TextView>(R.id.tvDetalleMarca)
        val tvModelo = view.findViewById<TextView>(R.id.tvDetalleModelo)
        val tvSerie = view.findViewById<TextView>(R.id.tvDetalleSerie)
        val tvEstado = view.findViewById<TextView>(R.id.tvDetalleEstado)
        val btnReportarNovedad = view.findViewById<Button>(R.id.btnReportarNovedadEquipo)

        equipo?.let { itEquipo ->
            tvCodigo.text = itEquipo.codigo
            tvTipo.text = itEquipo.tipo
            tvLaboratorio.text = itEquipo.laboratorioNombre ?: "Sin asignar"
            tvMarca.text = itEquipo.marca ?: "N/A"
            tvModelo.text = itEquipo.modelo ?: "N/A"
            tvSerie.text = itEquipo.numeroSerie ?: "N/A"
            tvEstado.text = itEquipo.estado.uppercase()

            btnReportarNovedad.setOnClickListener {
                val bundle = Bundle().apply { putLong("equipoId", itEquipo.id) }
                findNavController().navigate(R.id.action_detalleEquipoFragment_to_reportarNovedadFragment, bundle)
            }
        }
    }
}
