package com.uce.tesisrivasandrade.ui.main.registro_novedades

import android.graphics.Bitmap
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportarNovedadFragment : Fragment(R.layout.fragment_reportar_novedad) {

    private var _binding: FragmentReportarNovedadBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NovedadesViewModel by viewModels {
        NovedadViewModelFactory(NovedadRepository(ApiClient.getNovedadService(requireContext())))
    }

    private var selectedBitmap: Bitmap? = null
    private var listaLaboratorios: List<com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO> = emptyList()
    private var listaEquipos: List<GestionEquiposResponse> = emptyList()
    private var equipoPreseleccionado: GestionEquiposResponse? = null
    private var equipoId: Long = -1L

    private lateinit var imagePickerHelper: ImagePickerHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReportarNovedadBinding.bind(view)

        equipoId = arguments?.getLong("equipoId", -1L) ?: -1L

        imagePickerHelper = ImagePickerHelper(this) { bitmap ->
            binding.ivPreview.setImageBitmap(bitmap)
            binding.tvImageLabel.text = "Imagen seleccionada"
            selectedBitmap = bitmap // Guardamos el bitmap, no lo procesamos aún
        }

        if (equipoId != -1L) cargarEquipoPreseleccionado()

        setupDropdowns()
        setupListeners()
        setupObservers()
        cargarDatosIniciales()
    }

    private fun cargarEquipoPreseleccionado() {
        lifecycleScope.launch {
            val repo = GestionEquiposRepository(requireContext())
            repo.obtenerEquipoPorId(equipoId).onSuccess { equipo ->
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
            binding.tilEquipo.visibility = if (seleccionado == "EQUIPO") View.VISIBLE else View.GONE
            binding.etLaboratorio.isEnabled = true
        }

        val prioridades = listOf("BAJA", "MEDIA", "ALTA", "CRITICA")
        configurarDropdown(requireContext(), binding.etPrioridad, prioridades)
    }

    private fun setupListeners() {
        binding.btnSelectImage.setOnClickListener { imagePickerHelper.mostrarOpciones() }
        binding.btnGuardar.setOnClickListener { validarYGuardar() }
        binding.btnCancelar.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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

    private fun cargarDatosIniciales() {
        viewModel.cargarLaboratorios(GestionLaboratorios(requireContext()))
        viewModel.cargarEquipos(GestionEquiposRepository(requireContext()))
    }

    private fun validarYGuardar() {
        val titulo = binding.etTitulo.text.toString()
        val desc = binding.etDescripcion.text.toString()
        val labNombre = binding.etLaboratorio.text.toString()
        val prioridad = binding.etPrioridad.text.toString()
        val tipo = binding.etTipo.text.toString()

        if (titulo.isBlank() || desc.isBlank() || labNombre.isBlank() || prioridad.isBlank()) {
            Toast.makeText(context, "Llena los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val labId = listaLaboratorios.find { it.nombre == labNombre }?.id ?: equipoPreseleccionado?.laboratorioId
        if (labId == null) {
            Toast.makeText(requireContext(), "Laboratorio no válido", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            // Mostramos el cargando manualmente mientras procesamos la imagen
            binding.loadingLayout.root.visibility = View.VISIBLE
            
            val base64 = withContext(Dispatchers.Default) {
                selectedBitmap?.let { ImageUtils.encodeImageToBase64(it) }
            }

            val request = RegistroNovedadesRequest(
                titulo = titulo,
                descripcion = desc,
                tipo = tipo,
                prioridad = prioridad,
                laboratorioId = labId,
                equipoId = if (tipo == "EQUIPO") equipoPreseleccionado?.id else null,
                fechaReporte = DateUtils.getCurrentISODate(),
                imagenes = base64?.let { listOf(it) } ?: emptyList()
            )

            viewModel.reportarNovedad(request)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
