package com.uce.tesisrivasandrade.data.model.gestion_laboratorio

data class GestioLaboratoriosResponse(
    val id: Long,
    val nombre: String,
    val ubicacion: String?,
    val capacidad: Int?,
    val descripcion: String?,
    val activo: Boolean)
