package com.uce.tesisrivasandrade.data.remote

import com.uce.tesisrivasandrade.data.model.TokenResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface KeycloakApi {
    @FormUrlEncoded
    @POST("realms/siac-realm-test/protocol/openid-connect/token")
    fun login(
        @Field("grant_type") grantType: String = "password",
        @Field("client_id") clientId: String = "siac-app-movil",
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("scope") scope: String = "openid roles"
    ): Call<TokenResponse>

    @FormUrlEncoded
    @POST("realms/siac-realm-test/protocol/openid-connect/token")
    fun refreshToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("client_id") clientId: String = "siac-app-movil",
        @Field("refresh_token") refreshToken: String
    ): Call<TokenResponse>
}
