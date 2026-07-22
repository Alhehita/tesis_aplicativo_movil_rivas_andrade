package com.uce.tesisrivasandrade.data.model.registrouso

data class RegistroUsoEntradaRequest(
    val laboratorioId: Long,
    val laboratorioSecundarioId: Long? = null,
    val proposito: String,
    val observaciones: String?,
    val esExamen: Boolean = false
)