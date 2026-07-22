package com.uce.tesisrivasandrade.ui.main.registro_novedades

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse
import com.uce.tesisrivasandrade.data.model.registro_novedades.ImagenNovedadResponse
import com.uce.tesisrivasandrade.data.model.registro_novedades.RegistroNovedadesRequest
import com.uce.tesisrivasandrade.data.model.registro_novedades.RegistroNovedadesResponse
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.data.remote.registro_novedades.CambiarEstadoRequest
import com.uce.tesisrivasandrade.data.repository.GestionEquiposRepository
import com.uce.tesisrivasandrade.data.repository.GestionLaboratorios
import com.uce.tesisrivasandrade.data.repository.NovedadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NovedadesViewModel(
    private val repository: NovedadRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NovedadUiState())
    val uiState: StateFlow<NovedadUiState> = _uiState
    private val _laboratorios = MutableStateFlow<List<LaboratorioResponseDTO>>(emptyList())
    private val _equipos = MutableStateFlow<List<GestionEquiposResponse>>(emptyList())
    private var allNovedades: List<RegistroNovedadesResponse> = emptyList()
    
    val laboratorios: StateFlow<List<LaboratorioResponseDTO>> = _laboratorios
    val equipos: StateFlow<List<GestionEquiposResponse>> = _equipos
    
    private var currentFilter: String? = null

    fun obtenerNovedades(soloMias: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = if (soloMias) {
                    repository.obtenerMisNovedades()
                } else {
                    repository.listarTodas()
                }
                
                if (response.isSuccessful) {
                    allNovedades = response.body() ?: emptyList()
                    aplicarFiltro()
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun filtrarNovedades(estado: String?) {
        currentFilter = if (estado == "Todas") null else estado
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val filtradas = if (currentFilter == null) {
            allNovedades
        } else {
            allNovedades.filter { 
                it.estado?.equals(currentFilter, ignoreCase = true) == true
            }
        }
        _uiState.value = _uiState.value.copy(novedades = filtradas, isLoading = false)
    }

    fun reportarNovedad(request: RegistroNovedadesRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = repository.reportarNovedad(request)
                if (response.isSuccessful) {
                    val novedadCreada = response.body()
                    if (novedadCreada != null && request.imagenes.isNotEmpty()) {
                        request.imagenes.forEach { base64 ->
                            val imagenRequest = ImagenNovedadResponse(0, "novedad_${System.currentTimeMillis()}.jpg", "image/jpeg", base64)
                            repository.adjuntarImagen(novedadCreada.id, imagenRequest)
                        }
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Novedad reportada con éxito")
                    obtenerNovedades()
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Error al reportar: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun cambiarEstado(id: Long, nuevoEstado: String, observaciones: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = repository.cambiarEstado(id, nuevoEstado, observaciones)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Estado actualizado correctamente")
                    obtenerNovedades()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun cargarLaboratorios(repoLaboratorios: GestionLaboratorios) {
        viewModelScope.launch {
            repoLaboratorios.listarLaboratorios().onSuccess { _laboratorios.value = it }
        }
    }

    fun cargarEquipos(repoEquipos: GestionEquiposRepository) {
        viewModelScope.launch {
            repoEquipos.obtenerEquiposActivos().onSuccess { 
                _equipos.value = it 
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

class NovedadViewModelFactory(private val repository: NovedadRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NovedadesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NovedadesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
