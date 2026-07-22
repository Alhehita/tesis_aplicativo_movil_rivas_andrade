package com.uce.tesisrivasandrade.ui.main.registro_novedades

import com.uce.tesisrivasandrade.data.model.registro_novedades.RegistroNovedadesResponse

data class NovedadUiState(
    val isLoading: Boolean = false,
    val novedades: List<RegistroNovedadesResponse> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)
