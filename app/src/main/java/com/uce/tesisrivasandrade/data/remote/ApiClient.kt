package com.uce.tesisrivasandrade.data.remote

import android.content.Context
import com.uce.tesisrivasandrade.data.remote.registrouso.RegistroUsoApiService
import com.uce.tesisrivasandrade.data.remote.registro_novedades.NovedadApiService
import com.uce.tesisrivasandrade.utils.Constants
import com.uce.tesisrivasandrade.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var retrofit: Retrofit? = null
    
    val registroUsoApiService: RegistroUsoApiService by lazy {
        retrofit?.create(RegistroUsoApiService::class.java) 
            ?: throw IllegalStateException("Retrofit no inicializado. Llama a getRetrofit(context) primero.")
    }

    val novedadApiService: NovedadApiService by lazy {
        retrofit?.create(NovedadApiService::class.java)
            ?: throw IllegalStateException("Retrofit no inicializado. Llama a getRetrofit(context) primero.")
    }

    fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            val sessionManager = SessionManager(context)

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                    sessionManager.fetchToken()?.let {
                        request.addHeader("Authorization", "Bearer $it")
                    }
                    chain.proceed(request.build())
                }
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!
    }

    fun getClient(context: Context): Retrofit = getRetrofit(context)

    fun getRegistroUsoService(context: Context): RegistroUsoApiService {
        getRetrofit(context)
        return registroUsoApiService
    }

    fun getNovedadService(context: Context): NovedadApiService {
        getRetrofit(context)
        return novedadApiService
    }
}
