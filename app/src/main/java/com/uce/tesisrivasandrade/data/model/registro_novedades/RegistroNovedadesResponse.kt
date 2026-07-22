package com.uce.tesisrivasandrade.data.model.registro_novedades

import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse
import com.uce.tesisrivasandrade.data.model.gestion_laboratorio.GestioLaboratoriosResponse


data class RegistroNovedadesResponse(
    val id: Long,
    val titulo: String?,
    val descripcion: String?,
    val tipo: String?, // "EQUIPO" o "GENERAL"
    val estado: String?,
    val prioridad: String?,
    val usuarioReporta: String?,
    val laboratorio: GestioLaboratoriosResponse?,
    val laboratorioNombre: String?,
    val equipo: GestionEquiposResponse?,
    val equipoCodigo: String?,
    val fechaReporte: String?,
    val fechaResolucion: String?,
    val observacionesResolucion: String?,
    val imagenes: List<ImagenNovedadResponse>? = emptyList(),
    val createdAt: String?,
    val updatedAt: String?
)