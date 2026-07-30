package com.uce.tesisrivasandrade.data.remote

import android.content.Context
import com.uce.tesisrivasandrade.data.remote.registrouso.RegistroUsoApiService
import com.uce.tesisrivasandrade.data.remote.registro_novedades.NovedadApiService
import com.uce.tesisrivasandrade.utils.Constants
import com.uce.tesisrivasandrade.utils.SessionManager
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var retrofit: Retrofit? = null

    val registroUsoApiService: RegistroUsoApiService by lazy {
        retrofit?.create(RegistroUsoApiService::class.java)
            ?: throw IllegalStateException("Retrofit no inicializado.")
    }

    val novedadApiService: NovedadApiService by lazy {
        retrofit?.create(NovedadApiService::class.java)
            ?: throw IllegalStateException("Retrofit no inicializado.")
    }

    fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            val sessionManager = SessionManager(context)

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                    sessionManager.fetchToken()?.let {
                        request.addHeader("Authorization", "Bearer $it")
                    }
                    chain.proceed(request.build())
                }
                .authenticator(object : Authenticator {
                    override fun authenticate(route: Route?, response: Response): Request? {
                        // Evitar bucles infinitos si el refresh falla repetidamente
                        if (response.countHeaders("Authorization-Refresh-Attempt") > 0) return null

                        val refreshToken = sessionManager.fetchRefreshToken() ?: return null

                        // Llamada sincrónica a Keycloak para renovar el token
                        val keycloakApi = getKeycloakService()
                        val refreshResponse = keycloakApi.refreshToken(
                            refreshToken = refreshToken
                        ).execute()

                        return if (refreshResponse.isSuccessful) {
                            val newTokens = refreshResponse.body()
                            if (newTokens != null) {
                                sessionManager.saveToken(newTokens.accessToken)
                                sessionManager.saveRefreshToken(newTokens.refreshToken)

                                // Reintentar con el nuevo token
                                response.request.newBuilder()
                                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                                    .addHeader("Authorization-Refresh-Attempt", "1")
                                    .build()
                            } else null
                        } else {
                            sessionManager.clear()
                            null
                        }
                    }
                })
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!
    }

    // Instancia temporal de Retrofit para Keycloak
    private fun getKeycloakService(): KeycloakApi {
        return Retrofit.Builder()
            .baseUrl(Constants.KEYCLOAK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KeycloakApi::class.java)
    }

    fun getRegistroUsoService(context: Context): RegistroUsoApiService {
        getRetrofit(context)
        return registroUsoApiService
    }

    fun getNovedadService(context: Context): NovedadApiService {
        getRetrofit(context)
        return novedadApiService
    }
}

private fun Response.countHeaders(name: String): Int {
    return headers(name).size
}
