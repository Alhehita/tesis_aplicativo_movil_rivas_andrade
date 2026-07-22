package com.uce.tesisrivasandrade.utils

import android.util.Base64
import org.json.JSONObject

object JwtUtils {

    fun getRolesFromToken(token: String): List<String> {
        val parts = token.split(".")
        if (parts.size < 2) return emptyList()

        return try {
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)

            val realmAccess = json.optJSONObject("realm_access") ?: return emptyList()
            val roles = realmAccess.optJSONArray("roles") ?: return emptyList()

            val roleList = mutableListOf<String>()

            for (i in 0 until roles.length()) {
                val role = roles.getString(i)
                if (role.startsWith("ROLE_")) {
                    roleList.add(role)
                }
            }

            roleList
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getUsernameFromToken(token: String): String? {
        val parts = token.split(".")
        if (parts.size < 2) return null

        return try {
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            
            json.optString("preferred_username").takeIf { it.isNotBlank() }
                ?: json.optString("name").takeIf { it.isNotBlank() }
                ?: json.optString("sub")
        } catch (e: Exception) {
            null
        }
    }
}