package com.uce.tesisrivasandrade.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    private val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun formatFechaISO(fechaISO: String?): String {
        if (fechaISO.isNullOrBlank()) return "Sin fecha"

        // Limpieza para formatos LocalDateTime con microsegundos (truncamos después de los segundos)
        // Ejemplo: 2026-05-28T14:30:45.123456 -> 2026-05-28T14:30:45
        val cleanedDate = if (fechaISO.contains(".")) {
            fechaISO.substringBefore(".")
        } else {
            fechaISO
        }

        val formatos = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "dd/MM/yyyy HH:mm"
        )

        for (formato in formatos) {
            try {
                val sdf = SimpleDateFormat(formato, Locale.getDefault())
                val date = sdf.parse(cleanedDate)
                if (date != null) return outputFormat.format(date)
            } catch (e: Exception) { continue }
        }

        // Si es un número (timestamp largo)
        try {
            val millis = fechaISO.toLongOrNull()
            if (millis != null) {
                return outputFormat.format(Date(millis))
            }
        } catch (e: Exception) { }

        // Fallback final: devolver algo legible si es posible
        return cleanedDate.replace("T", " ").take(16)
    }

    fun getCurrentISODate(): String {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return isoFormat.format(Date())
    }
}
