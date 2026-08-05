package com.uce.tesisrivasandrade.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.LoggedInUserView
import com.uce.tesisrivasandrade.data.remote.ApiClient
import com.uce.tesisrivasandrade.databinding.ActivityLoginBinding
import com.uce.tesisrivasandrade.ui.main.MainActivity
import com.uce.tesisrivasandrade.utils.SessionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val btnSettings = binding.btnSettings

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        // Configurar botón de IP
        btnSettings.setOnClickListener {
            showIpConfigDialog()
        }

        // Observar cambios del formulario con StateFlow
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginFormState.collectLatest { loginState ->
                    login.isEnabled = loginState.isDataValid

                    if (loginState.usernameError != null) {
                        username.error = getString(loginState.usernameError)
                    }
                    if (loginState.passwordError != null) {
                        password.error = getString(loginState.passwordError)
                    }
                }
            }
        }

        // Observar resultado del login con StateFlow
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginResult.collectLatest { loginResult ->
                    loginResult ?: return@collectLatest

                    if (loginResult.error != null) {
                        showLoginFailed(loginResult.error)
                        loading.visibility = View.GONE
                    }

                    if (loginResult.success != null) {
                        updateUiWithUser(loginResult.success)
                    }

                    loginViewModel.clearLoginResult()
                }
            }
        }

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        loading.visibility = View.VISIBLE
                        loginViewModel.login(
                            this@LoginActivity,
                            username.text.toString(),
                            password.text.toString()
                        )
                    }
                }
                false
            }
        }

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.login(
                this@LoginActivity,
                username.text.toString(),
                password.text.toString()
            )
        }
    }

    private fun showIpConfigDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_config_ip, null)
        val etIp = dialogView.findViewById<TextInputEditText>(R.id.et_ip_dinamica)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btn_guardar)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btn_cancelar)

        etIp.setText(sessionManager.fetchServerUrl())

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Hacer que el fondo del diálogo sea transparente para que se vea el blanco redondeado
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnGuardar.setOnClickListener {
            val newUrl = etIp.text.toString()
            if (newUrl.isNotEmpty()) {
                sessionManager.saveServerUrl(newUrl)
                ApiClient.reiniciarConfiguracion()
                Toast.makeText(this, "Servidor actualizado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        sessionManager.saveToken(model.token)
        sessionManager.saveRefreshToken(model.refreshToken)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}