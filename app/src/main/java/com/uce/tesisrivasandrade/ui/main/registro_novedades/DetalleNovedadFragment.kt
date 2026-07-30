package com.uce.tesisrivasandrade.ui.main.registro_novedades

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.registro_novedades.ImagenNovedadResponse
import com.uce.tesisrivasandrade.data.model.registro_novedades.RegistroNovedadesResponse
import com.uce.tesisrivasandrade.data.remote.ApiClient
import com.uce.tesisrivasandrade.data.repository.NovedadRepository
import com.uce.tesisrivasandrade.databinding.FragmentDetalleNovedadBinding
import com.uce.tesisrivasandrade.utils.DateUtils
import com.uce.tesisrivasandrade.utils.ImagePickerHelper
import com.uce.tesisrivasandrade.utils.ImageUtils
import com.uce.tesisrivasandrade.utils.SessionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetalleNovedadFragment : Fragment(R.layout.fragment_detalle_novedad) {

    private var _binding: FragmentDetalleNovedadBinding? = null
    private val binding get() = _binding!!
    private var novedad: RegistroNovedadesResponse? = null
    private var novedadId: Long = -1L

    private val TAG = "DetalleNovedad"

    private val viewModel: NovedadesViewModel by viewModels {
        NovedadViewModelFactory(NovedadRepository(ApiClient.getNovedadService(requireContext())))
    }

    private lateinit var imagePickerHelper: ImagePickerHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetalleNovedadBinding.bind(view)

        novedadId = arguments?.getLong("novedadId", -1L) ?: -1L

        imagePickerHelper = ImagePickerHelper(this) { bitmap ->
            subirBitmapComoImagen(bitmap)
        }

        setupUI()
        setupObservers()
        cargarNovedadPorId(novedadId)
    }

    private fun cargarNovedadPorId(id: Long) {
        lifecycleScope.launch {
            try {
                mostrarCargando(true)
                val repo = NovedadRepository(ApiClient.getNovedadService(requireContext()))
                val response = repo.obtenerPorId(id)
                if (response.isSuccessful) {
                    novedad = response.body()
                    actualizarUI()
                    cargarImagenes(id)
                } else {
                    Toast.makeText(requireContext(), "Error al cargar novedad", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar novedad", e)
            } finally {
                mostrarCargando(false)
            }
        }
    }

    private suspend fun cargarImagenes(id: Long) {
        try {
            val repo = NovedadRepository(ApiClient.getNovedadService(requireContext()))
            val response = repo.obtenerImagenes(id)
            if (response.isSuccessful) {
                val imagenes = response.body() ?: emptyList()
                novedad = novedad?.copy(imagenes = imagenes)
                actualizarUI()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar imágenes", e)
        }
    }

    private fun setupUI() {
        val sessionManager = SessionManager(requireContext())
        if (sessionManager.esAdmin()) {
            binding.cardGestionNovedad.visibility = View.VISIBLE
            setupGestionForm()
        }
        binding.btnAgregarFoto.setOnClickListener {
            imagePickerHelper.mostrarOpciones("Agregar Evidencia")
        }
    }

    private fun setupGestionForm() {
        val estados = arrayOf("PENDIENTE", "EN_PROCESO", "SOLUCIONADA")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, estados)
        binding.etNuevoEstado.setAdapter(adapter)
        binding.btnActualizarEstado.setOnClickListener {
            val nuevoEstado = binding.etNuevoEstado.text.toString()
            val observaciones = binding.etObservacionesResolucion.text.toString()
            if (nuevoEstado.isNotBlank() && nuevoEstado != "-- Seleccione --") {
                novedad?.id?.let { viewModel.cambiarEstado(it, nuevoEstado, observaciones) }
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    // Esto asegura que si el ViewModel está cargando, se muestre el icono
                    if (state.isLoading) mostrarCargando(true) else mostrarCargando(false)
                    
                    if (state.successMessage != null) {
                        Toast.makeText(requireContext(), state.successMessage, Toast.LENGTH_SHORT).show()
                        viewModel.clearMessages()
                        recargarNovedad()
                    }
                    state.error?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        viewModel.clearMessages()
                    }
                }
            }
        }
    }

    private fun actualizarUI() {
        val n = novedad ?: return
        binding.tvDetalleTitulo.text = n.titulo
        binding.tvDetalleDescripcion.text = n.descripcion
        binding.chipDetalleEstado.text = n.estado
        binding.chipDetallePrioridad.text = n.prioridad
        binding.tvDetalleLaboratorio.text = n.laboratorio?.nombre ?: n.laboratorioNombre ?: "Sin laboratorio"
        binding.tvDetalleFecha.text = "Reportado el ${DateUtils.formatFechaISO(n.fechaReporte ?: "")}"

        binding.layoutImagenes.removeAllViews()
        val imagenes = n.imagenes ?: emptyList()
        binding.tvNoImagenes.visibility = if (imagenes.isEmpty()) View.VISIBLE else View.GONE
        
        imagenes.forEach { img ->
            if (img.imagenBase64.isNullOrBlank()) return@forEach
            val iv = ImageView(requireContext()).apply {
                val size = (150 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply { setMargins(0, 0, 16, 0) }
                scaleType = ImageView.ScaleType.CENTER_CROP
                try {
                    val pureBase64 = img.imagenBase64.substringAfter(",").replace("\n", "").trim()
                    val bitmap = ImageUtils.decodeBase64ToImage(pureBase64)
                    setImageBitmap(bitmap)
                    setOnClickListener { mostrarImagenGrande(bitmap) }
                } catch (e: Exception) {
                    setImageResource(android.R.drawable.stat_notify_error)
                }
            }
            binding.layoutImagenes.addView(iv)
        }
    }

    private fun recargarNovedad() {
        if (novedadId != -1L) cargarNovedadPorId(novedadId)
    }

    private fun mostrarImagenGrande(bitmap: android.graphics.Bitmap) {
        val iv = ImageView(requireContext())
        iv.setImageBitmap(bitmap)
        AlertDialog.Builder(requireContext()).setView(iv).setPositiveButton("Cerrar", null).show()
    }

    private fun subirBitmapComoImagen(bitmap: android.graphics.Bitmap) {
        val n = novedad ?: return
        
        // MOSTRAR CARGANDO INMEDIATAMENTE
        mostrarCargando(true)
        
        lifecycleScope.launch {
            try {
                // El procesamiento (compresión) se hace en segundo plano
                val base64 = ImageUtils.encodeImageToBase64(bitmap)
                val imgRequest = ImagenNovedadResponse(0, "extra_${System.currentTimeMillis()}.jpg", "image/jpeg", base64)
                
                val repo = NovedadRepository(ApiClient.getNovedadService(requireContext()))
                val result = repo.adjuntarImagen(n.id, imgRequest)
                
                if (result.isSuccessful) {
                    Toast.makeText(requireContext(), "¡Imagen agregada!", Toast.LENGTH_SHORT).show()
                    recargarNovedad()
                } else {
                    Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    mostrarCargando(false)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                mostrarCargando(false)
            }
            // El ocultar cargando se maneja en recargarNovedad -> cargarNovedadPorId o en los catches
        }
    }

    private fun mostrarCargando(show: Boolean) {
        binding.loadingLayout.root.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
