package com.uce.tesisrivasandrade.ui.main.gestion_laboratorios

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioRequestDTO
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.data.repository.GestionLaboratorios
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GestionLaboratoriosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GestionLaboratorios(application)

    private val _laboratorios = MutableStateFlow<List<LaboratorioResponseDTO>>(emptyList())
    val laboratorios: StateFlow<List<LaboratorioResponseDTO>> = _laboratorios

    private val _laboratoriosEliminados = MutableStateFlow<List<LaboratorioResponseDTO>>(emptyList())
    val laboratoriosEliminados: StateFlow<List<LaboratorioResponseDTO>> = _laboratoriosEliminados

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarLaboratorios() {
        viewModelScope.launch {
            val result = repository.listarActivos()
            result.onSuccess {
                _laboratorios.value = it
            }.onFailure {
                _mensaje.value = "Error al cargar laboratorios: ${it.message}"
            }
        }
    }

    fun cargarLaboratoriosEliminados() {
        viewModelScope.launch {
            val result = repository.listarEliminados()
            result.onSuccess {
                _laboratoriosEliminados.value = it
            }.onFailure {
                _mensaje.value = "Error al cargar papelera de laboratorios: ${it.message}"
            }
        }
    }

    fun crearLaboratorio(request: LaboratorioRequestDTO) {
        viewModelScope.launch {
            val result = repository.crear(request)
            result.onSuccess {
                _mensaje.value = "Laboratorio creado con éxito"
                cargarLaboratorios()
            }.onFailure {
                _mensaje.value = "Error al crear laboratorio: ${it.message}"
            }
        }
    }

    fun actualizarLaboratorio(id: Long, request: LaboratorioRequestDTO) {
        viewModelScope.launch {
            val result = repository.actualizar(id, request)
            result.onSuccess {
                _mensaje.value = "Laboratorio actualizado con éxito"
                cargarLaboratorios()
            }.onFailure {
                _mensaje.value = "Error al actualizar laboratorio: ${it.message}"
            }
        }
    }

    fun eliminarLaboratorio(id: Long) {
        viewModelScope.launch {
            val result = repository.eliminarLogico(id)
            result.onSuccess {
                _mensaje.value = "Laboratorio movido a la papelera"
                cargarLaboratorios()
            }.onFailure {
                _mensaje.value = "Error al eliminar laboratorio: ${it.message}"
            }
        }
    }

    fun restaurarLaboratorio(id: Long) {
        viewModelScope.launch {
            val result = repository.restaurar(id)
            result.onSuccess {
                _mensaje.value = "Laboratorio restaurado con éxito"
                cargarLaboratoriosEliminados()
                cargarLaboratorios()
            }.onFailure {
                _mensaje.value = "Error al restaurar laboratorio: ${it.message}"
            }
        }
    }

    fun eliminarPermanente(id: Long) {
        viewModelScope.launch {
            val result = repository.eliminarPermanente(id)
            result.onSuccess {
                _mensaje.value = "Laboratorio eliminado permanentemente"
                cargarLaboratoriosEliminados()
            }.onFailure {
                _mensaje.value = "Error al eliminar permanentemente: ${it.message}"
            }
        }
    }

    fun clearMensaje() {
        _mensaje.value = null
    }
}
