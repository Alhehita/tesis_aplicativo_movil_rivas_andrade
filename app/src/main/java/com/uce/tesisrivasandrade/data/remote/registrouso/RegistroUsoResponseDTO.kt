package com.uce.tesisrivasandrade.data.remote.registrouso

data class RegistroUsoResponseDTO(

    val id: Long,

    val usuarioId: Long,

    val usuarioNombre: String,

    val laboratorioId: Long,

    val laboratorioNombre: String,

    val fechaEntrada: String,

    val fechaSalida: String?,

    val proposito: String?,

    val observaciones: String?,

    val duracionMinutos: Long?,

    val activo: Boolean,

    val createdAt: String
)