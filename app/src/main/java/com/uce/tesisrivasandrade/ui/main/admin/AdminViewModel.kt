package com.uce.tesisrivasandrade.ui.main.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uce.tesisrivasandrade.ui.main.profesor.ProfesorUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfesorUiState())
    val uiState: StateFlow<ProfesorUiState> = _uiState

    fun cargarDatos(username: String? = null) {
        _uiState.value = ProfesorUiState(isLoading = true)
        viewModelScope.launch {
            try {
                // Simulamos carga de datos iniciales
                delay(800)
                val bienvenido = if (username != null) "Bienvenido, $username 🎓" else "Bienvenido Admin 🎓"
                _uiState.value = ProfesorUiState(
                    mensaje = bienvenido
                )
            } catch (e: Exception) {
                _uiState.value = ProfesorUiState(error = "Error al inicializar panel")
            }
        }
    }
}