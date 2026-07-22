package com.uce.tesisrivasandrade.ui.main.gestion_laboratorios

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.utils.SessionManager
import kotlinx.coroutines.launch

class DetalleLaboratorioFragment : Fragment(R.layout.fragment_detalle_laboratorio) {

    private var laboratorio: LaboratorioResponseDTO? = null
    private val viewModel: GestionLaboratoriosViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val puedeReservar = sessionManager.esProfesor() || sessionManager.esAdmin() || sessionManager.esDirector()

        // Obtener laboratorioId de argumentos de navegación
        val laboratorioId = arguments?.getLong("laboratorioId", -1L) ?: -1L

        // Buscar laboratorio en el ViewModel
        if (laboratorioId != -1L) {
            lifecycleScope.launch {
                viewModel.laboratorios.collect { lista ->
                    val encontrado = lista.find { it.id == laboratorioId }
                    if (encontrado != null) {
                        laboratorio = encontrado
                        actualizarUI(view, encontrado, puedeReservar)
                        return@collect
                    }
                }
            }
        }

        // La navegación hacia atrás usa el botón del toolbar (setupActionBarWithNavController)
    }

    private fun actualizarUI(view: View, lab: LaboratorioResponseDTO, puedeReservar: Boolean) {
        val tvNombre = view.findViewById<TextView>(R.id.tvDetalleNombreLab)
        val tvUbicacion = view.findViewById<TextView>(R.id.tvDetalleUbicacion)
        val tvCapacidad = view.findViewById<TextView>(R.id.tvDetalleCapacidad)
        val tvEstado = view.findViewById<TextView>(R.id.tvDetalleEstado)
        val btnReservar = view.findViewById<MaterialButton>(R.id.btnReservarLab)

        tvNombre.text = lab.nombre
        tvUbicacion.text = lab.ubicacion ?: "Sin ubicación"
        tvCapacidad.text = "${lab.capacidad ?: 0} personas"
        tvEstado.text = "ACTIVO"

        btnReservar.visibility = if (puedeReservar) View.VISIBLE else View.GONE

        btnReservar.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("isModoSalida", false)
                putLong("laboratorioId", lab.id)
            }
            findNavController().navigate(R.id.action_detalleLaboratorioFragment_to_registroUsoFragment, bundle)
        }
    }
}
