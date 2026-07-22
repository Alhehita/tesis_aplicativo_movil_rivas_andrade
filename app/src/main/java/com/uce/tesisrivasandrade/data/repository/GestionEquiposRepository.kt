package com.uce.tesisrivasandrade.data.repository

import android.content.Context
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposRequest
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse
import com.uce.tesisrivasandrade.data.remote.ApiClient
import com.uce.tesisrivasandrade.data.remote.gestionEquipos.GestionEquiposApiService

class GestionEquiposRepository(context: Context) {

    private val api = ApiClient.getRetrofit(context).create(GestionEquiposApiService::class.java)

    suspend fun obtenerEquipos(): Result<List<GestionEquiposResponse>>{
        return try {
            val response = api.obtenerTodosEquipos()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun agregarEquipo(request: GestionEquiposRequest): Result<GestionEquiposResponse> {
        return try {
            val response = api.crearEquipo(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarEquipo(id: Long): Result<Unit> {
        return try {
            api.eliminarEquipo(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restaurarEquipo(id: Long): Result<GestionEquiposResponse> {
        return try {
            val response = api.restaurarEquipo(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarPermanentementeEquipo(id: Long): Result<Unit> {
        return try {
            api.eliminarPermanentementeEquipo(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerEquiposActivos(): Result<List<GestionEquiposResponse>> {
        return try {
            val response = api.obtenerEquiposActivos()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerEquiposEliminados(): Result<List<GestionEquiposResponse>> {
        return try {
            val response = api.obtenerEquiposEliminados()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
        }

    suspend fun obtenerEquipoPorId(id: Long): Result<GestionEquiposResponse> {
        return try {
            val response = api.obtenerEquipoPorId(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarEquipo(id: Long, equipo: GestionEquiposResponse): Result<GestionEquiposResponse> {
        return try {
            val response = api.actualizarEquipos(id, equipo)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun obtenerEquiposPorLaboratorio(labId: Long): Result<List<GestionEquiposResponse>> {
        return try {
            val response = api.obtenerEquiposPorLaboratorio(labId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
