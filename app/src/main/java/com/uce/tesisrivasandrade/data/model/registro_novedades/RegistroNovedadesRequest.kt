package com.uce.tesisrivasandrade.data.model.registro_novedades

import com.google.gson.annotations.SerializedName

data class RegistroNovedadesRequest(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("prioridad") val prioridad: String,
    @SerializedName("laboratorioId") val laboratorioId: Long,
    @SerializedName("equipoId") val equipoId: Long?,
    @SerializedName("fechaReporte") val fechaReporte: String,
    @SerializedName("imagenes") val imagenes: List<String>
)
