package com.uce.tesisrivasandrade.ui.main.gestion_equipos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposRequest
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.data.repository.GestionLaboratorios
import kotlinx.coroutines.launch

class EditarEquipoFragment : Fragment(R.layout.fragment_editar_equipo) {

    private val viewModel: GestionEquiposViewModel by activityViewModels {
        GestionEquiposViewModel.factory(requireContext())
    }
    private var equipo: GestionEquiposResponse? = null
    private var listaLaboratorios: List<LaboratorioResponseDTO> = emptyList()
    private var selectedLaboratorioId: Long? = null
    private var equipoId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener equipoId de argumentos de navegación
        equipoId = arguments?.getLong("equipoId", -1L) ?: -1L

        val tvTitulo = view.findViewById<TextView>(R.id.tvTitleEditar)
        val etCodigo = view.findViewById<TextInputEditText>(R.id.etEditarCodigo)
        val etTipo = view.findViewById<TextInputEditText>(R.id.etEditarTipo)
        val etEstado = view.findViewById<AutoCompleteTextView>(R.id.etEditarEstado)
        val etMarca = view.findViewById<TextInputEditText>(R.id.etEditarMarca)
        val etModelo = view.findViewById<TextInputEditText>(R.id.etEditarModelo)
        val etSerie = view.findViewById<TextInputEditText>(R.id.etEditarSerie)
        val etLaboratorio = view.findViewById<AutoCompleteTextView>(R.id.etEditarLaboratorio)
        
        val btnGuardar = view.findViewById<MaterialButton>(R.id.btnGuardar)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btnCancelar)

        // Forzar color negro
        val colorNegro = ContextCompat.getColor(requireContext(), android.R.color.black)
        etCodigo.setTextColor(colorNegro)
        etTipo.setTextColor(colorNegro)
        etEstado.setTextColor(colorNegro)
        etMarca.setTextColor(colorNegro)
        etModelo.setTextColor(colorNegro)
        etSerie.setTextColor(colorNegro)
        etLaboratorio.setTextColor(colorNegro)

        // Lógica dinámica para Títulos (Barra Azul y Tarjeta)
        if (equipoId != -1L) {
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Editar Equipo"
            tvTitulo?.text = "Editar Equipo"
            btnGuardar.text = "Guardar Cambios"
            
            lifecycleScope.launch {
                viewModel.equipos.collect { lista ->
                    val encontrado = lista.find { it.id == equipoId }
                    if (encontrado != null) {
                        equipo = encontrado
                        etCodigo.setText(encontrado.codigo)
                        etTipo.setText(encontrado.tipo)
                        etEstado.setText(encontrado.estado.uppercase(), false)
                        etMarca.setText(encontrado.marca)
                        etModelo.setText(encontrado.modelo)
                        etSerie.setText(encontrado.numeroSerie)
                        etLaboratorio.setText(encontrado.laboratorioNombre ?: "Sin asignar", false)
                        selectedLaboratorioId = encontrado.laboratorioId
                    }
                }
            }
        } else {
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Agregar Equipo"
            tvTitulo?.text = "Agregar Nuevo Equipo"
            btnGuardar.text = "Crear Equipo"
        }

        // Configurar dropdown de estados
        val estados = arrayOf("OPERATIVO", "DAÑADO", "MANTENIMIENTO")
        etEstado.setAdapter(ArrayAdapter(requireContext(), R.layout.item_dropdown, estados))

        cargarLaboratorios(etLaboratorio)

        btnGuardar.setOnClickListener {
            val codigo = etCodigo.text.toString()
            val tipo = etTipo.text.toString()
            val estado = etEstado.text.toString()
            val marca = etMarca.text.toString()
            val modelo = etModelo.text.toString()
            val serie = etSerie.text.toString()

            if (codigo.isBlank() || tipo.isBlank() || selectedLaboratorioId == null) {
                Toast.makeText(requireContext(), "Código, Tipo y Laboratorio son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (equipo == null) {
                val request = GestionEquiposRequest(
                    codigo = codigo,
                    tipo = tipo,
                    marca = marca,
                    modelo = modelo,
                    numeroSerie = serie,
                    estado = estado,
                    laboratorioId = selectedLaboratorioId
                )
                viewModel.crearEquipo(request)
            } else {
                val equipoActualizado = equipo!!.copy(
                    codigo = codigo,
                    tipo = tipo,
                    estado = estado,
                    marca = marca,
                    modelo = modelo,
                    numeroSerie = serie,
                    laboratorioId = selectedLaboratorioId
                )
                viewModel.actualizarEquipo(equipoActualizado)
            }
        }

        btnCancelar.setOnClickListener { findNavController().navigateUp() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mensaje.collect { mensaje ->
                mensaje?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    if (it.contains("éxito") || it.contains("correctamente")) {
                        findNavController().navigateUp()
                        viewModel.clearMensaje()
                    }
                }
            }
        }
    }

    private fun cargarLaboratorios(autoComplete: AutoCompleteTextView) {
        val repo = GestionLaboratorios(requireContext())
        lifecycleScope.launch {
            repo.listarActivos().onSuccess { lista ->
                listaLaboratorios = lista
                val nombres = lista.map { it.nombre }
                val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, nombres)
                autoComplete.setAdapter(adapter)

                autoComplete.setOnItemClickListener { parent, _, position, _ ->
                    val nombreSeleccionado = parent.getItemAtPosition(position).toString()
                    selectedLaboratorioId = listaLaboratorios.find { it.nombre == nombreSeleccionado }?.id
                }
            }
        }
    }
}
