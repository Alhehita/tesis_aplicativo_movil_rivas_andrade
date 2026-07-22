package com.uce.tesisrivasandrade.data.repository

import android.content.Context
import com.uce.tesisrivasandrade.data.model.TokenResponse
import com.uce.tesisrivasandrade.data.remote.LoginDataSource
import com.uce.tesisrivasandrade.data.remote.Result

class LoginRepository(private val dataSource: LoginDataSource) {

    fun login(
        context: Context,
        username: String,
        password: String,
        callback: (Result<TokenResponse>) -> Unit
    ) {
        dataSource.login(context, username, password, callback)
    }

    fun logout() {
        // luego limpiamos sesión aquí
    }
}