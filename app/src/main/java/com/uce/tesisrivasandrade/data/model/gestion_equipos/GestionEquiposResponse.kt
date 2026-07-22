package com.uce.tesisrivasandrade.data.model.gestion_equipos

import java.io.Serializable

data class GestionEquiposResponse(
    val id: Long,
    val codigo: String,
    val tipo: String,
    val marca : String?,
    val modelo: String?,
    val numeroSerie: String?,
    val estado: String,
    val laboratorioId: Long?,
    val laboratorioNombre: String?,
    val createdAt: String?,
) : Serializable
