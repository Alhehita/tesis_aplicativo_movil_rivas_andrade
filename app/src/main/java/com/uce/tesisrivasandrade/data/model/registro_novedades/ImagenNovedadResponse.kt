package com.uce.tesisrivasandrade.data.model.registro_novedades

data class ImagenNovedadResponse(
    val id: Long,
    val nombreArchivo: String,
    val tipoMime: String,
    val imagenBase64: String
)
