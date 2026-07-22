package com.uce.tesisrivasandrade.ui.main.registrouso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uce.tesisrivasandrade.data.model.registrouso.RegistroUsoSalidaRequest
import com.uce.tesisrivasandrade.data.remote.registrouso.RegistroUsoResponseDTO
import com.uce.tesisrivasandrade.data.repository.RegistroUsoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrarUsoMenuViewModel(private val repository: RegistroUsoRepository) : ViewModel() {
    private val _registros = MutableStateFlow<List<RegistroUsoResponseDTO>>(emptyList())
    val registros: StateFlow<List<RegistroUsoResponseDTO>> = _registros

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _isLoading = MutableStateFlow(false)

    fun cargarRegistros(verTodos: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = if (verTodos) {
                repository.todosLosRegistros() // Llama a api/registros/todos
            } else {
                repository.obtenerMisRegistros() // Llama a api/registros/mis-registros
            }

            result.onSuccess { lista ->
                val listaOrdenada = lista.sortedWith(
                    compareByDescending<RegistroUsoResponseDTO> { it.activo }
                        .thenByDescending { it.fechaEntrada }
                )
                _registros.value = listaOrdenada
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun registrarSalida(observaciones: String?) {
        viewModelScope.launch {
            val request = RegistroUsoSalidaRequest(observaciones = observaciones)
            val result = repository.registrarSalida(request)

            result.onSuccess {
                _mensaje.value = "Salida registrada correctamente"
                cargarRegistros() // Recargar la lista después de la salida
            }.onFailure {
                _error.value = it.message ?: "Error al registrar salida"
            }
        }
    }
}