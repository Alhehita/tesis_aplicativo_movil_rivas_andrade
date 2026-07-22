package com.uce.tesisrivasandrade.data.remote.gestionLaboratorios

import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioRequestDTO
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GestionLaboratoriosApiService {

    @GET("api/laboratorios")
    suspend fun obtenerTodos(): List<LaboratorioResponseDTO>

    @GET("api/laboratorios/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): LaboratorioResponseDTO

    @POST("api/laboratorios")
    suspend fun crear(@Body request: LaboratorioRequestDTO): LaboratorioResponseDTO

    @PUT("api/laboratorios/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body request: LaboratorioRequestDTO
    ): LaboratorioResponseDTO

    @DELETE("api/laboratorios/{id}")
    suspend fun softDelete(@Path("id") id: Long): Response<Void>

    @POST("api/laboratorios/{id}/restore")
    suspend fun restore(@Path("id") id: Long): LaboratorioResponseDTO

    @GET("api/laboratorios/activos")
    suspend fun obtenerActivos(): List<LaboratorioResponseDTO>

    @GET("api/laboratorios/eliminados")
    suspend fun obtenerEliminados(): List<LaboratorioResponseDTO>

    @DELETE("api/laboratorios/{id}/permanent")
    suspend fun hardDelete(@Path("id") id: Long): Response<Void>
}
