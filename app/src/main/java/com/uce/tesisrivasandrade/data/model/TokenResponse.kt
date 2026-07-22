package com.uce.tesisrivasandrade.data.model

import com.google.gson.annotations.SerializedName

data class TokenResponse(

    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("expires_in")
    val expiresIn: Int
)