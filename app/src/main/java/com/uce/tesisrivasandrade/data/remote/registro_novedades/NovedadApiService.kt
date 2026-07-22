package com.uce.tesisrivasandrade.data.remote.registro_novedades

import com.uce.tesisrivasandrade.data.model.registro_novedades.ImagenNovedadResponse
import com.uce.tesisrivasandrade.data.model.registro_novedades.RegistroNovedadesRequest
import com.uce.tesisrivasandrade.data.model.registro_novedades.RegistroNovedadesResponse
import retrofit2.Response
import retrofit2.http.*

interface NovedadApiService {

    // GET /api/novedades
    @GET("api/novedades")
    suspend fun listarTodas(): Response<List<RegistroNovedadesResponse>>

    // GET /api/novedades/{id}
    @GET("api/novedades/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<RegistroNovedadesResponse>

    // GET /api/novedades/mis-novedades
    @GET("api/novedades/mis-novedades")
    suspend fun obtenerMisNovedades(): Response<List<RegistroNovedadesResponse>>

    // GET /api/novedades/estado/{estado}
    @GET("api/novedades/estado/{estado}")
    suspend fun listarPorEstado(@Path("estado") estado: String): Response<List<RegistroNovedadesResponse>>

    // POST /api/novedades
    @POST("api/novedades")
    suspend fun reportarNovedad(@Body request: RegistroNovedadesRequest): Response<RegistroNovedadesResponse>

    // PUT /api/novedades/{id}/estado
    @PUT("api/novedades/{id}/estado")
    suspend fun cambiarEstado(
        @Path("id") id: Long,
        @Body request: CambiarEstadoRequest
    ): Response<RegistroNovedadesResponse>

    // POST /api/novedades/{id}/imagenes
    @POST("api/novedades/{id}/imagenes")
    suspend fun adjuntarImagen(
        @Path("id") id: Long,
        @Body request: ImagenNovedadResponse
    ): Response<ImagenNovedadResponse>

    // GET /api/novedades/{id}/imagenes
    @GET("api/novedades/{id}/imagenes")
    suspend fun obtenerImagenes(@Path("id") id: Long): Response<List<ImagenNovedadResponse>>
}

// DTOs adicionales necesarios para que coincidan con el backend
data class CambiarEstadoRequest(
    val nuevoEstado: String,
    val observaciones: String? = null
)
