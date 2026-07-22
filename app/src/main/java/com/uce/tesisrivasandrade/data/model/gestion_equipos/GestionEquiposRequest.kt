package com.uce.tesisrivasandrade.data.model.gestion_equipos

data class GestionEquiposRequest(
    val codigo: String,
    val tipo: String,
    val marca: String?,
    val modelo: String?,
    val numeroSerie: String?,
    val estado: String,
    val laboratorioId: Long?
)
