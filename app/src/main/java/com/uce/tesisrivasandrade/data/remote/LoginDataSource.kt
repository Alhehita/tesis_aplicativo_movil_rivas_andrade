package com.uce.tesisrivasandrade.data.remote

import android.content.Context
import com.uce.tesisrivasandrade.data.model.LoginRequest
import com.uce.tesisrivasandrade.data.remote.ApiClient
import com.uce.tesisrivasandrade.data.model.TokenResponse
import com.uce.tesisrivasandrade.data.remote.AuthApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginDataSource {

    fun login(
        context: Context,
        username: String,
        password: String,
        callback: (Result<TokenResponse>) -> Unit
    ) {

        val retrofit = ApiClient.getRetrofit(context)
        val authApi = retrofit.create(AuthApiService::class.java)

        val request = LoginRequest(username, password)

        val call = authApi.login(request)

        call.enqueue(object : Callback<TokenResponse> {

            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.Success(it))
                    } ?: callback(Result.Error(IOException("Empty response")))
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("LOGIN_ERROR", "Code: ${response.code()} - $errorBody")
                    callback(Result.Error(IOException("Login failed: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                android.util.Log.e("LOGIN_FAILURE", t.message ?: "Unknown error")
                callback(Result.Error(IOException("Error", t)))
            }
        })
    }
    fun logout() {
        // TODO: revoke token
    }
}
