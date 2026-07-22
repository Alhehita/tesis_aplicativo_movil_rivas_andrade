package com.uce.tesisrivasandrade.data.remote.gestionEquipos

import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Body
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposRequest
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface GestionEquiposApiService {
    @GET("api/equipos")
    suspend fun obtenerTodosEquipos(): List<GestionEquiposResponse>

    @GET("api/equipos/{id}")
    suspend fun obtenerEquipoPorId(@Path("id") id: Long): GestionEquiposResponse

    @GET("api/equipos/laboratorio/{id}")
    suspend fun obtenerEquiposPorLaboratorio(@Path("id") labId: Long): List<GestionEquiposResponse>

    @POST("api/equipos")
    suspend fun crearEquipo(@Body request: GestionEquiposRequest): GestionEquiposResponse

    @PUT("api/equipos/{id}")
    suspend fun actualizarEquipos(@Path("id") id: Long, @Body equipo: GestionEquiposResponse): GestionEquiposResponse

    @DELETE("api/equipos/{id}")
    suspend fun eliminarEquipo(@Path("id") id: Long)

    @POST("api/equipos/{id}/restore")
    suspend fun restaurarEquipo(@Path("id") id: Long): GestionEquiposResponse

    @GET("api/equipos/activos")
    suspend fun obtenerEquiposActivos(): List<GestionEquiposResponse>

    @GET("api/equipos/eliminados")
    suspend fun obtenerEquiposEliminados(): List<GestionEquiposResponse>

    @DELETE("api/equipos/{id}/permanent")
    suspend fun eliminarPermanentementeEquipo(@Path("id") id: Long)
}
