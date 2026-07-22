package com.uce.tesisrivasandrade.data.repository

import com.uce.tesisrivasandrade.data.model.registro_novedades.ImagenNovedadResponse
import com.uce.tesisrivasandrade.data.model.registro_novedades.RegistroNovedadesRequest
import com.uce.tesisrivasandrade.data.model.registro_novedades.RegistroNovedadesResponse
import com.uce.tesisrivasandrade.data.remote.registro_novedades.CambiarEstadoRequest
import com.uce.tesisrivasandrade.data.remote.registro_novedades.NovedadApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class NovedadRepository(private val apiService: NovedadApiService) {

    suspend fun listarTodas(): Response<List<RegistroNovedadesResponse>> = withContext(Dispatchers.IO) {
        apiService.listarTodas()
    }

    suspend fun obtenerMisNovedades(): Response<List<RegistroNovedadesResponse>> = withContext(Dispatchers.IO) {
        apiService.obtenerMisNovedades()
    }

    suspend fun reportarNovedad(request: RegistroNovedadesRequest): Response<RegistroNovedadesResponse> = withContext(Dispatchers.IO) {
        apiService.reportarNovedad(request)
    }

    suspend fun adjuntarImagen(id: Long, imagen: ImagenNovedadResponse): Response<ImagenNovedadResponse> = withContext(Dispatchers.IO) {
        apiService.adjuntarImagen(id, imagen)
    }

    suspend fun obtenerImagenes(id: Long): Response<List<ImagenNovedadResponse>> = withContext(Dispatchers.IO) {
        apiService.obtenerImagenes(id)
    }

    suspend fun obtenerPorId(id: Long): Response<RegistroNovedadesResponse> = withContext(Dispatchers.IO) {
        apiService.obtenerPorId(id)
    }

    suspend fun cambiarEstado(id: Long, nuevoEstado: String, observaciones: String?): Response<RegistroNovedadesResponse> = withContext(Dispatchers.IO) {
        apiService.cambiarEstado(id, CambiarEstadoRequest(nuevoEstado, observaciones))
    }
}
