package com.uce.tesisrivasandrade.ui.main.profesor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfesorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfesorUiState())
    val uiState: StateFlow<ProfesorUiState> = _uiState

    fun cargarDatos(username: String? = null) {
        _uiState.value = ProfesorUiState(isLoading = true)

        viewModelScope.launch {
            try {
                // Simulamos llamada a API
                delay(1000)
                val bienvenido = if (username != null) "Bienvenido, $username 👨‍🏫" else "Bienvenido Profesor 👨‍🏫"
                _uiState.value = ProfesorUiState(
                    mensaje = bienvenido
                )

            } catch (e: Exception) {
                _uiState.value = ProfesorUiState(
                    error = "Error al cargar datos"
                )
            }
        }
    }
}