package com.uce.tesisrivasandrade.data.remote

import com.uce.tesisrivasandrade.data.model.LoginRequest
import com.uce.tesisrivasandrade.data.model.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<TokenResponse>

    @POST("api/auth/refresh")
    fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Call<TokenResponse>
}
