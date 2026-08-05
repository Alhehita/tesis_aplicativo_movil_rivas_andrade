package com.uce.tesisrivasandrade.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SessionManager(context: Context) {

    private val prefs = EncryptedSharedPreferences.create(
        "app_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveServerUrl(url: String) {
        var formattedUrl = url.trim()
        if (formattedUrl.isNotEmpty()) {
            if (!formattedUrl.startsWith("http")) {
                formattedUrl = "http://$formattedUrl"
            }
            if (!formattedUrl.endsWith("/")) {
                formattedUrl = "$formattedUrl/"
            }
            prefs.edit().putString("BASE_URL_DINAMICA", formattedUrl).apply()
        }
    }

    fun fetchServerUrl(): String {
        return prefs.getString("BASE_URL_DINAMICA", Constants.DEFAULT_BASE_URL) ?: Constants.DEFAULT_BASE_URL
    }

    fun saveToken(token: String) {
        prefs.edit().putString("JWT_TOKEN", token).apply()
    }

    fun saveRefreshToken(refreshToken: String) {
        prefs.edit().putString("REFRESH_TOKEN", refreshToken).apply()
    }

    fun fetchToken(): String? {
        return prefs.getString("JWT_TOKEN", null)
    }

    fun fetchRefreshToken(): String? {
        return prefs.getString("REFRESH_TOKEN", null)
    }

    fun getRoles(): List<Rol> {
        val token = fetchToken() ?: return emptyList()
        return JwtUtils.getRolesFromToken(token).map { Rol.fromString(it) }
    }

    fun getUsername(): String? {
        val token = fetchToken() ?: return null
        return JwtUtils.getUsernameFromToken(token)
    }

    fun tieneRol(rol: Rol): Boolean {
        return getRoles().contains(rol)
    }

    fun esAdmin(): Boolean = tieneRol(Rol.ADMIN)
    fun esDirector(): Boolean = tieneRol(Rol.DIRECTOR)
    fun esProfesor(): Boolean = tieneRol(Rol.PROFESOR)
    fun esTecnico(): Boolean = tieneRol(Rol.TECNICO)

    // Permisos centralizados
    fun puedeGestionarEquipos(): Boolean = esAdmin() || esDirector()
    fun puedeVerTodosLosRegistros(): Boolean = esAdmin() || esDirector()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
