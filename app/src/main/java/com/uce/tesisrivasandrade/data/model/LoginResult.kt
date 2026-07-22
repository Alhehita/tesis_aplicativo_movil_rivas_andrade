package com.uce.tesisrivasandrade.data.model

data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)