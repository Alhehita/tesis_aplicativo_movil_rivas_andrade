package com.uce.tesisrivasandrade.utils

enum class Rol {
    ADMIN,
    TECNICO, // Representa al rol "ROLE_USUARIO" que te dio tu Ing.
    PROFESOR,
    DIRECTOR;

    companion object {
        fun fromString(role: String): Rol {
            return when (role.uppercase()) {
                "ROLE_ADMIN" -> ADMIN
                "ROLE_PROFESOR" -> PROFESOR
                "ROLE_FACULTAD_DIRECTOR_CARRERA" -> DIRECTOR
                "ROLE_USUARIO" -> TECNICO // Mapeo solicitado por el Ing.
                else -> PROFESOR // Valor por defecto
            }
        }
    }
}
