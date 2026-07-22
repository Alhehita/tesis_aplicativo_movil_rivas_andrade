package com.uce.tesisrivasandrade.data.repository

import android.content.Context
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.data.model.registrouso.RegistroUsoEntradaRequest
import com.uce.tesisrivasandrade.data.model.registrouso.RegistroUsoResponse
import com.uce.tesisrivasandrade.data.model.registrouso.RegistroUsoSalidaRequest
import com.uce.tesisrivasandrade.data.remote.ApiClient
import com.uce.tesisrivasandrade.data.remote.registrouso.RegistroUsoApiService
import com.uce.tesisrivasandrade.data.remote.registrouso.RegistroUsoResponseDTO
import org.json.JSONObject
import retrofit2.Response

class RegistroUsoRepository(private val context: Context) {

    private val api: RegistroUsoApiService =
        ApiClient
            .getRetrofit(context)
            .create(RegistroUsoApiService::class.java)

    suspend fun registrarEntrada(
        request: RegistroUsoEntradaRequest
    ): Result<RegistroUsoResponse> {

        return try {
            val response = api.registrarEntrada(request) //ENVIAR TOKEN PARA LA SERGURIDAD. REVISAR QUE SEA RESOURCE SERVER

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = parseErrorBody(response)
                Result.failure(Exception(errorMsg))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarSalida(
        request: RegistroUsoSalidaRequest
    ): Result<RegistroUsoResponse> {

        return try {
            val response = api.registrarSalida(request)

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = parseErrorBody(response)
                Result.failure(Exception(errorMsg))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorBody(response: Response<*>): String {
        return try {
            val jsonObject = JSONObject(response.errorBody()?.string() ?: "{}")
            // Intenta obtener el campo 'message' que suele enviar Spring Boot
            jsonObject.optString("message", "Error desconocido (${response.code()})")
        } catch (e: Exception) {
            "Error en la respuesta del servidor"
        }
    }

    suspend fun obtenerMisRegistros(): Result<List<RegistroUsoResponseDTO>> {
        return try {
            val response = api.obtenerMisRegistros()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerLaboratorios(): Result<List<LaboratorioResponseDTO>> {
        return try {
            val response = api.obtenerLaboratorios()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun todosLosRegistros(): Result<List<RegistroUsoResponseDTO>> {
        return try {
            val response = api.obtenerTodosRegistros()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
