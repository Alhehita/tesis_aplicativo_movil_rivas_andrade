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
import com.google.android.material.textfield.TextInputEditText
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioRequestDTO
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import kotlinx.coroutines.launch

class EditarLaboratorioFragment : Fragment(R.layout.fragment_editar_laboratorio) {

    private val viewModel: GestionLaboratoriosViewModel by activityViewModels()
    private var laboratorio: LaboratorioResponseDTO? = null
    private var laboratorioId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener laboratorioId de argumentos de navegación
        laboratorioId = arguments?.getLong("laboratorioId", -1L) ?: -1L

        val tvTitulo = view.findViewById<TextView>(R.id.tvTitleEditarLab)
        val etNombre = view.findViewById<TextInputEditText>(R.id.etNombreLab)
        val etUbicacion = view.findViewById<TextInputEditText>(R.id.etUbicacionLab)
        val etCapacidad = view.findViewById<TextInputEditText>(R.id.etCapacidadLab)
        
        val btnGuardar = view.findViewById<MaterialButton>(R.id.btnGuardar)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btnCancelar)

        // Si editar, buscar laboratorio por ID en el ViewModel
        if (laboratorioId != -1L) {
            lifecycleScope.launch {
                viewModel.laboratorios.collect { lista ->
                    val encontrado = lista.find { it.id == laboratorioId }
                    if (encontrado != null) {
                        laboratorio = encontrado
                        tvTitulo.text = "Editar Laboratorio"
                        btnGuardar.text = "Guardar Cambios"
                        etNombre.setText(encontrado.nombre)
                        etUbicacion.setText(encontrado.ubicacion)
                        etCapacidad.setText(encontrado.capacidad?.toString())
                        return@collect
                    }
                }
            }
        } else {
            tvTitulo.text = "Agregar Nuevo Laboratorio"
            btnGuardar.text = "Crear Laboratorio"
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val ubicacion = etUbicacion.text.toString()
            val capacidadStr = etCapacidad.text.toString()

            if (nombre.isBlank()) {
                Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LaboratorioRequestDTO(
                nombre = nombre,
                ubicacion = ubicacion.ifBlank { null },
                capacidad = capacidadStr.toIntOrNull()
            )

            if (laboratorio == null) {
                viewModel.crearLaboratorio(request)
            } else {
                viewModel.actualizarLaboratorio(laboratorio!!.id, request)
            }
        }

        val actionCerrar = { findNavController().navigateUp() }
        btnCancelar.setOnClickListener { actionCerrar() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mensaje.collect { mensaje ->
                mensaje?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    if (it.contains("éxito") || it.contains("correctamente")) {
                        actionCerrar()
                        viewModel.clearMensaje()
                    }
                }
            }
        }
        if (laboratorioId != -1L) {lifecycleScope.launch {
            viewModel.laboratorios.collect { lista ->
                val encontrado = lista.find { it.id == laboratorioId }
                if (encontrado != null) {
                    laboratorio = encontrado
                    tvTitulo.text = "Editar Laboratorio"
                    // AGREGAR ESTA LÍNEA para la barra azul:
                    (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = "Editar Laboratorio"

                    btnGuardar.text = "Guardar Cambios"
                    etNombre.setText(encontrado.nombre)
                    etUbicacion.setText(encontrado.ubicacion)
                    etCapacidad.setText(encontrado.capacidad?.toString())
                    return@collect
                }
            }
        }
        } else {
            tvTitulo.text = "Agregar Nuevo Laboratorio"
            // AGREGAR ESTA LÍNEA para la barra azul:
            (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = "Agregar Laboratorio"

            btnGuardar.text = "Crear Laboratorio"
        }
    }
}
