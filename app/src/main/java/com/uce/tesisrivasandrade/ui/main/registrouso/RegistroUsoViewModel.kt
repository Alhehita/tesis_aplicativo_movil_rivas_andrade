package com.uce.tesisrivasandrade.ui.main.registrouso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.data.model.registrouso.RegistroUsoEntradaRequest
import com.uce.tesisrivasandrade.data.model.registrouso.RegistroUsoSalidaRequest
import com.uce.tesisrivasandrade.data.repository.RegistroUsoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroUsoViewModel(
    private val repository: RegistroUsoRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _laboratorios = MutableStateFlow<List<LaboratorioResponseDTO>>(emptyList())
    val laboratorios: StateFlow<List<LaboratorioResponseDTO>> = _laboratorios

    fun cargarLaboratorios() {
        viewModelScope.launch {
            val labsResult = repository.obtenerLaboratorios()
            val registrosResult = repository.obtenerMisRegistros()

            labsResult.onSuccess { todosLosLabs ->
                registrosResult.onSuccess { registros ->
                    val idsLaboratoriosActivos = registros
                        .filter { it.activo }
                        .map { it.laboratorioId }
                        .toSet()

                    _laboratorios.value = todosLosLabs.filter { it.id !in idsLaboratoriosActivos }
                    
                }.onFailure {
                    _laboratorios.value = todosLosLabs
                    _mensaje.value = "Error al verificar estados: ${it.message}"
                }
            }.onFailure {
                _mensaje.value = "Error al cargar laboratorios: ${it.message}"
            }
        }
    }

    fun registrarEntrada(
        laboratorioId: Long,
        laboratorioSecundarioId: Long? = null,
        proposito: String,
        observaciones: String?,
        esExamen: Boolean
    ) {
        viewModelScope.launch {
            val request = RegistroUsoEntradaRequest(
                laboratorioId = laboratorioId,
                laboratorioSecundarioId = laboratorioSecundarioId,
                proposito = proposito,
                observaciones = observaciones,
                esExamen = esExamen
            )

            val result = repository.registrarEntrada(request)

            result.onSuccess {
                _mensaje.value = "Entrada registrada correctamente"
                cargarLaboratorios()
            }.onFailure {
                _mensaje.value = it.message ?: "Error al registrar entrada"
            }
        }
    }

    fun registrarSalida(observaciones: String?) {
        viewModelScope.launch {
            val request = RegistroUsoSalidaRequest(
                observaciones = observaciones
            )

            val result = repository.registrarSalida(request)

            result.onSuccess {
                _mensaje.value = "Salida registrada correctamente"
                cargarLaboratorios()
            }.onFailure {
                _mensaje.value = it.message ?: "Error al registrar salida"
            }
        }
    }

    fun clearMensaje() {
        _mensaje.value = null
    }
}
