package com.uce.tesisrivasandrade.data.remote.registrouso

import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.data.model.registrouso.RegistroUsoEntradaRequest
import com.uce.tesisrivasandrade.data.model.registrouso.RegistroUsoResponse
import com.uce.tesisrivasandrade.data.model.registrouso.RegistroUsoSalidaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface RegistroUsoApiService {

    @POST("api/registros/entrada")
    suspend fun registrarEntrada(
        @Body request: RegistroUsoEntradaRequest
    ): Response<RegistroUsoResponse>

    @PUT("api/registros/salida")
    suspend fun registrarSalida(
        @Body request: RegistroUsoSalidaRequest
    ): Response<RegistroUsoResponse>

    @GET("api/registros/mis-registros")
    suspend fun obtenerMisRegistros(): List<RegistroUsoResponseDTO>

    @GET("api/laboratorios")
    suspend fun obtenerLaboratorios(): List<LaboratorioResponseDTO>

    //ver todos

    @GET("api/registros/todos")
    suspend fun obtenerTodosRegistros(): List<RegistroUsoResponseDTO>
}