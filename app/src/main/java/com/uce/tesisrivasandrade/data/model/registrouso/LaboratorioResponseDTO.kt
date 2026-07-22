package com.uce.tesisrivasandrade.data.model.registrouso

data class LaboratorioResponseDTO(
    val id: Long,
    val nombre: String,
    val ubicacion: String?,
    val capacidad: Int?
) {
    override fun toString(): String {
        return nombre
    }
}