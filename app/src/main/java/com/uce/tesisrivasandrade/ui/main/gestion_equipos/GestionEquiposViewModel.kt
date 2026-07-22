package com.uce.tesisrivasandrade.ui.main.gestion_equipos

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposRequest
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse
import com.uce.tesisrivasandrade.data.repository.GestionEquiposRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GestionEquiposViewModel(
    private val repository: GestionEquiposRepository
) : ViewModel() {

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val repo = GestionEquiposRepository(context)
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GestionEquiposViewModel(repo) as T
                }
            }
        }
    }

    private val _equipos = MutableStateFlow<List<GestionEquiposResponse>>(emptyList())
    val equipos: StateFlow<List<GestionEquiposResponse>> = _equipos

    private val _equiposEliminados = MutableStateFlow<List<GestionEquiposResponse>>(emptyList())
    val equiposEliminados: StateFlow<List<GestionEquiposResponse>> = _equiposEliminados

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje
    
    private var allEquipos: List<GestionEquiposResponse> = emptyList()

    fun cargarEquipos() {
        viewModelScope.launch {
            val result = repository.obtenerEquiposActivos()
            result.onSuccess {
                allEquipos = it
                _equipos.value = it
            }.onFailure {
                _mensaje.value = "Error al cargar equipos: ${it.message}"
            }
        }
    }

    fun cargarEquiposEliminados() {
        viewModelScope.launch {
            val result = repository.obtenerEquiposEliminados()
            result.onSuccess {
                _equiposEliminados.value = it
            }.onFailure {
                _mensaje.value = "Error al cargar papelera de equipos: ${it.message}"
            }
        }
    }

    fun filtrarPorLaboratorio(nombreLaboratorio: String?) {
        if (nombreLaboratorio.isNullOrBlank() || nombreLaboratorio == "Todos") {
            _equipos.value = allEquipos
        } else {
            _equipos.value = allEquipos.filter { 
                it.laboratorioNombre?.equals(nombreLaboratorio, ignoreCase = true) == true 
            }
        }
    }

    fun buscarPorCodigo(query: String) {
        if (query.isBlank()) {
            _equipos.value = allEquipos
        } else {
            _equipos.value = allEquipos.filter { 
                it.codigo.contains(query, ignoreCase = true) 
            }
        }
    }

    fun crearEquipo(request: GestionEquiposRequest) {
        viewModelScope.launch {
            val result = repository.agregarEquipo(request)
            result.onSuccess {
                _mensaje.value = "Equipo creado correctamente"
                cargarEquipos()
            }.onFailure {
                _mensaje.value = "Error al crear: ${it.message}"
            }
        }
    }

    fun actualizarEquipo(equipo: GestionEquiposResponse) {
        viewModelScope.launch {
            val result = repository.actualizarEquipo(equipo.id, equipo)
            result.onSuccess {
                _mensaje.value = "Equipo actualizado correctamente"
                cargarEquipos()
            }.onFailure {
                _mensaje.value = "Error al actualizar: ${it.message}"
            }
        }
    }

    fun eliminarEquipo(id: Long) {
        viewModelScope.launch {
            val result = repository.eliminarEquipo(id)
            result.onSuccess {
                _mensaje.value = "Equipo movido a la papelera"
                cargarEquipos()
            }.onFailure {
                _mensaje.value = "Error al eliminar: ${it.message}"
            }
        }
    }

    fun restaurarEquipo(id: Long) {
        viewModelScope.launch {
            val result = repository.restaurarEquipo(id)
            result.onSuccess {
                _mensaje.value = "Equipo restaurado con éxito"
                cargarEquiposEliminados()
                cargarEquipos()
            }.onFailure {
                _mensaje.value = "Error al restaurar equipo: ${it.message}"
            }
        }
    }

    fun eliminarPermanente(id: Long) {
        viewModelScope.launch {
            val result = repository.eliminarPermanentementeEquipo(id)
            result.onSuccess {
                _mensaje.value = "Equipo eliminado permanentemente"
                cargarEquiposEliminados()
            }.onFailure {
                _mensaje.value = "Error al eliminar permanentemente: ${it.message}"
            }
        }
    }

    fun clearMensaje() {
        _mensaje.value = null
    }
}
