package com.uce.tesisrivasandrade.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.uce.tesisrivasandrade.data.remote.Result
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.LoggedInUserView
import com.uce.tesisrivasandrade.data.model.LoginResult
import com.uce.tesisrivasandrade.data.model.TokenResponse
import com.uce.tesisrivasandrade.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableStateFlow(LoginFormState())
    val loginFormState: StateFlow<LoginFormState> = _loginForm.asStateFlow()

    private val _loginResult = MutableStateFlow<LoginResult?>(null)
    val loginResult: StateFlow<LoginResult?> = _loginResult.asStateFlow()

    fun login(context: Context, username: String, password: String) {
        loginRepository.login(context, username, password) { result ->
            if (result is Result.Success) {
                val data = result.data as TokenResponse
                _loginResult.value = LoginResult(
                    success = LoggedInUserView(
                        displayName = username,
                        token = data.accessToken,
                        refreshToken = data.refreshToken
                    )
                )
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
