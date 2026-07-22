package com.uce.tesisrivasandrade.ui.main.registro_novedades

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse
import com.uce.tesisrivasandrade.data.model.registro_novedades.RegistroNovedadesRequest
import com.uce.tesisrivasandrade.data.remote.ApiClient
import com.uce.tesisrivasandrade.data.repository.GestionEquiposRepository
import com.uce.tesisrivasandrade.data.repository.GestionLaboratorios
import com.uce.tesisrivasandrade.data.repository.NovedadRepository
import com.uce.tesisrivasandrade.databinding.FragmentReportarNovedadBinding
import com.uce.tesisrivasandrade.utils.DateUtils
import com.uce.tesisrivasandrade.utils.ImagePickerHelper
import com.uce.tesisrivasandrade.utils.ImageUtils
import com.uce.tesisrivasandrade.utils.configurarDropdown
import kotlinx.coroutines.launch

class ReportarNovedadFragment : Fragment(R.layout.fragment_reportar_novedad) {

    private var _binding: FragmentReportarNovedadBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NovedadesViewModel by viewModels {
        NovedadViewModelFactory(NovedadRepository(ApiClient.getNovedadService(requireContext())))
    }

    private var base64Image: String? = null
    private var listaLaboratorios: List<com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO> = emptyList()
    private var listaEquipos: List<GestionEquiposResponse> = emptyList()
    private var equipoPreseleccionado: GestionEquiposResponse? = null
    private var equipoId: Long = -1L

    private lateinit var imagePickerHelper: ImagePickerHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReportarNovedadBinding.bind(view)

        // Obtener equipoId de argumentos de navegación
        equipoId = arguments?.getLong("equipoId", -1L) ?: -1L

        imagePickerHelper = ImagePickerHelper(this) { bitmap ->
            binding.ivPreview.setImageBitmap(bitmap)
            binding.tvImageLabel.text = "Imagen seleccionada"
            base64Image = ImageUtils.encodeImageToBase64(bitmap)
        }

        // Si hay equipoId, cargar equipo preseleccionado
        if (equipoId != -1L) {
            cargarEquipoPreseleccionado()
        }

        setupDropdowns()
        setupListeners()
        setupObservers()
        cargarDatosIniciales()
    }

    private fun cargarEquipoPreseleccionado() {
        lifecycleScope.launch {
            val repo = GestionEquiposRepository(requireContext())
            val result = repo.obtenerEquipoPorId(equipoId)
            result.onSuccess { equipo ->
                equipoPreseleccionado = equipo
                binding.etTitulo.setText("Novedad en equipo: ${equipo.codigo}")
                binding.etLaboratorio.setText(equipo.laboratorioNombre, false)
                binding.etLaboratorio.isEnabled = false
                binding.etTipo.setText("EQUIPO", false)
                binding.etTipo.isEnabled = false
                binding.tilEquipo.visibility = View.GONE
            }
        }
    }

    private fun setupDropdowns() {
        val tipos = if (equipoPreseleccionado != null) listOf("EQUIPO") else listOf("GENERAL", "EQUIPO")
        configurarDropdown(requireContext(), binding.etTipo, tipos)

        if (equipoPreseleccionado == null) {
            binding.etTipo.setText("GENERAL", false)
            binding.tilEquipo.visibility = View.GONE
        }

        binding.etTipo.setOnItemClickListener { _, _, _, _ ->
            val seleccionado = binding.etTipo.text.toString()
            if (seleccionado == "EQUIPO") {
                binding.tilEquipo.visibility = View.VISIBLE
                binding.tilEquipo.isEnabled = false
                binding.etEquipo.setText("", false)
                binding.etLaboratorio.setText("", false)
                binding.etLaboratorio.isEnabled = true
            } else {
                binding.tilEquipo.visibility = View.GONE
                binding.etLaboratorio.isEnabled = true
                binding.etLaboratorio.setText("", false)
            }
        }

        binding.etLaboratorio.setOnItemClickListener { _, _, _, _ ->
            val seleccionado = binding.etTipo.text.toString()
            val labNombre = binding.etLaboratorio.text.toString()
            val lab = listaLaboratorios.find { it.nombre == labNombre }

            if (seleccionado == "EQUIPO" && lab != null) {
                val equiposFiltrados = listaEquipos.filter { it.laboratorioId == lab.id }
                if (equiposFiltrados.isEmpty()) {
                    Toast.makeText(requireContext(), "No hay equipos registrados en este laboratorio", Toast.LENGTH_SHORT).show()
                    binding.etEquipo.setText("", false)
                    binding.tilEquipo.isEnabled = false
                } else {
                    val nombresEquipos = equiposFiltrados.map { "${it.codigo} - ${it.tipo}" }
                    configurarDropdown(requireContext(), binding.etEquipo, nombresEquipos)
                    binding.etEquipo.setText("", false)
                    binding.tilEquipo.isEnabled = true
                }
            }
        }

        val prioridades = listOf("BAJA", "MEDIA", "ALTA", "CRITICA")
        configurarDropdown(requireContext(), binding.etPrioridad, prioridades)
    }

    private fun setupListeners() {
        binding.btnSelectImage.setOnClickListener { imagePickerHelper.mostrarOpciones() }
        binding.btnGuardar.setOnClickListener { validarYGuardar() }
        binding.btnCancelar.setOnClickListener { findNavController().navigateUp() }
    }

    private fun cargarDatosIniciales() {
        viewModel.cargarLaboratorios(GestionLaboratorios(requireContext()))
        viewModel.cargarEquipos(GestionEquiposRepository(requireContext()))
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.laboratorios.collect { laboratorios ->
                        listaLaboratorios = laboratorios
                        val nombres = laboratorios.map { it.nombre }
                        configurarDropdown(requireContext(), binding.etLaboratorio, nombres)
                        equipoPreseleccionado?.let { binding.etLaboratorio.setText(it.laboratorioNombre, false) }
                    }
                }

                launch {
                    viewModel.equipos.collect { equipos ->
                        listaEquipos = equipos
                    }
                }

                launch {
                    viewModel.uiState.collect { state ->
                        binding.loadingLayout.root.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                        if (state.successMessage != null) {
                            Toast.makeText(requireContext(), state.successMessage, Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                            viewModel.clearMessages()
                        }
                        state.error?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            viewModel.clearMessages()
                        }
                    }
                }
            }
        }
    }

    private fun validarYGuardar() {
        val titulo = binding.etTitulo.text.toString()
        val desc = binding.etDescripcion.text.toString()
        val labNombre = binding.etLaboratorio.text.toString()
        val prioridad = binding.etPrioridad.text.toString()
        val tipo = binding.etTipo.text.toString()
        val equipoNombre = binding.etEquipo.text.toString()

        if (titulo.isBlank() || desc.isBlank() || labNombre.isBlank() || prioridad.isBlank()) {
            Toast.makeText(context, "Por favor llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val equipoIdEnvio = if (tipo == "EQUIPO") {
            equipoPreseleccionado?.id ?: listaEquipos.find { "${it.codigo} - ${it.tipo}" == equipoNombre }?.id
        } else null

        if (tipo == "EQUIPO" && equipoIdEnvio == null) {
            Toast.makeText(requireContext(), "Por favor, seleccione un equipo válido", Toast.LENGTH_SHORT).show()
            return
        }

        val labId = listaLaboratorios.find { it.nombre == labNombre }?.id
            ?: equipoPreseleccionado?.laboratorioId
            ?: run {
                Toast.makeText(requireContext(), "Error: Laboratorio no identificado", Toast.LENGTH_SHORT).show()
                return
            }

        val request = RegistroNovedadesRequest(
            titulo = titulo,
            descripcion = desc,
            tipo = tipo,
            prioridad = prioridad,
            laboratorioId = labId,
            equipoId = equipoIdEnvio,
            fechaReporte = DateUtils.getCurrentISODate(),
            imagenes = base64Image?.let { listOf(it) } ?: emptyList()
        )

        viewModel.reportarNovedad(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
