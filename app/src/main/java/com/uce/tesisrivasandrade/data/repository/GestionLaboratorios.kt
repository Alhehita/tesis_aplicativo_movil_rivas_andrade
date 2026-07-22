package com.uce.tesisrivasandrade.data.repository

import android.content.Context
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioRequestDTO
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.data.remote.ApiClient
import com.uce.tesisrivasandrade.data.remote.gestionLaboratorios.GestionLaboratoriosApiService

class GestionLaboratorios(private val context: Context) {

    private val api: GestionLaboratoriosApiService =
        ApiClient.getRetrofit(context)
            .create(GestionLaboratoriosApiService::class.java)

    suspend fun listarLaboratorios(): Result<List<LaboratorioResponseDTO>> {
        return try {
            val response = api.obtenerTodos()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPorId(id: Long): Result<LaboratorioResponseDTO> {
        return try {
            val response = api.obtenerPorId(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crear(request: LaboratorioRequestDTO): Result<LaboratorioResponseDTO> {
        return try {
            val response = api.crear(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizar(id: Long, request: LaboratorioRequestDTO): Result<LaboratorioResponseDTO> {
        return try {
            val response = api.actualizar(id, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarLogico(id: Long): Result<Unit> {
        return try {
            api.softDelete(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restaurar(id: Long): Result<LaboratorioResponseDTO> {
        return try {
            val response = api.restore(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listarActivos(): Result<List<LaboratorioResponseDTO>> {
        return try {
            val response = api.obtenerActivos()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listarEliminados(): Result<List<LaboratorioResponseDTO>> {
        return try {
            val response = api.obtenerEliminados()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarPermanente(id: Long): Result<Unit> {
        return try {
            api.hardDelete(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
