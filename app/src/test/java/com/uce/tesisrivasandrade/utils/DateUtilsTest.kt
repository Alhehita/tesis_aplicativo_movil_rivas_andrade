package com.uce.tesisrivasandrade.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {

    @Test
    fun `formatFechaISO con fecha ISO completa debe retornar fecha formateada`() {
        val resultado = DateUtils.formatFechaISO("2026-07-15T10:30:00")
        assertEquals("15/07/2026 10:30", resultado)
    }

    @Test
    fun `formatFechaISO con microsegundos debe limpiarlos y formatear`() {
        val resultado = DateUtils.formatFechaISO("2026-05-28T14:30:45.123456")
        assertEquals("28/05/2026 14:30", resultado)
    }

    @Test
    fun `formatFechaISO con formato dd MM yyyy debe mantener el formato`() {
        val resultado = DateUtils.formatFechaISO("15/07/2026 10:30")
        assertEquals("15/07/2026 10:30", resultado)
    }

    @Test
    fun `formatFechaISO con solo fecha yyyy-MM-dd debe formatear`() {
        val resultado = DateUtils.formatFechaISO("2026-07-15")
        assertEquals("15/07/2026 00:00", resultado)
    }

    @Test
    fun `formatFechaISO con string vacio debe retornar Sin fecha`() {
        val resultado = DateUtils.formatFechaISO("")
        assertEquals("Sin fecha", resultado)
    }

    @Test
    fun `formatFechaISO con null debe retornar Sin fecha`() {
        val resultado = DateUtils.formatFechaISO(null)
        assertEquals("Sin fecha", resultado)
    }

    @Test
    fun `formatFechaISO con string invalido debe retornar fallback legible`() {
        val resultado = DateUtils.formatFechaISO("texto-invalido")
        assertEquals("texto-invalido", resultado)
    }

    @Test
    fun `getCurrentISODate debe retornar fecha con formato ISO`() {
        val resultado = DateUtils.getCurrentISODate()
        // Debe contener el separador "T" de ISO
        assertTrue(resultado.contains("T"))
        // Debe tener al menos 16 caracteres (yyyy-MM-ddTHH:mm)
        assertTrue(resultado.length >= 16)
    }
}
