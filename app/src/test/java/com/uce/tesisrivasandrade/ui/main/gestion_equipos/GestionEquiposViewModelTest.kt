package com.uce.tesisrivasandrade.ui.main.gestion_equipos

import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse
import com.uce.tesisrivasandrade.data.repository.GestionEquiposRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GestionEquiposViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<GestionEquiposRepository>()
    private lateinit var viewModel: GestionEquiposViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GestionEquiposViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarEquipos debe actualizar la lista cuando la API responde OK`() = runTest {
        // 1. PREPARAR: Simular respuesta exitosa del repositorio
        val equiposMock = listOf(
            crearEquipoMock(id = 1, codigo = "PC-01", tipo = "PC"),
            crearEquipoMock(id = 2, codigo = "LAP-01", tipo = "LAPTOP")
        )
        coEvery { mockRepository.obtenerEquiposActivos() } returns Result.success(equiposMock)

        // 2. EJECUTAR
        viewModel.cargarEquipos()
        advanceUntilIdle()

        // 3. VERIFICAR
        val equipos = viewModel.equipos.value
        assertEquals(2, equipos.size)
        assertEquals("PC-01", equipos[0].codigo)
        assertEquals("LAP-01", equipos[1].codigo)
    }

    @Test
    fun `cargarEquipos debe mostrar error cuando la API falla`() = runTest {
        // 1. PREPARAR: Simular error
        coEvery { mockRepository.obtenerEquiposActivos() } returns Result.failure(
            Exception("Error de conexión")
        )

        // 2. EJECUTAR
        viewModel.cargarEquipos()
        advanceUntilIdle()

        // 3. VERIFICAR
        assertTrue(viewModel.equipos.value.isEmpty())
        assertNotNull(viewModel.mensaje.value)
        assertTrue(viewModel.mensaje.value!!.contains("Error"))
    }

    private fun crearEquipoMock(
        id: Long,
        codigo: String,
        tipo: String,
        labNombre: String? = null
    ) = GestionEquiposResponse(
        id = id,
        codigo = codigo,
        tipo = tipo,
        marca = null,
        modelo = null,
        numeroSerie = null,
        estado = "OPERATIVO",
        laboratorioId = null,
        laboratorioNombre = labNombre,
        createdAt = null
    )

    @Test
    fun `filtrarPorLaboratorio debe filtrar equipos correctamente`() = runTest {
        // 1. PREPARAR: Cargar equipos iniciales
        val equiposMock = listOf(
            crearEquipoMock(id = 1, codigo = "PC-01", tipo = "PC", labNombre = "Lab Redes"),
            crearEquipoMock(id = 2, codigo = "PC-02", tipo = "PC", labNombre = "Lab Software"),
            crearEquipoMock(id = 3, codigo = "LAP-01", tipo = "LAPTOP", labNombre = "Lab Redes")
        )
        coEvery { mockRepository.obtenerEquiposActivos() } returns Result.success(equiposMock)

        viewModel.cargarEquipos()
        advanceUntilIdle()

        // 2. EJECUTAR: Filtrar por "Lab Redes"
        viewModel.filtrarPorLaboratorio("Lab Redes")

        // 3. VERIFICAR
        val filtrados = viewModel.equipos.value
        assertEquals(2, filtrados.size)
        assertTrue(filtrados.all { it.laboratorioNombre == "Lab Redes" })
    }

    @Test
    fun `filtrarPorLaboratorio con Todos debe mostrar todos los equipos`() = runTest {
        // 1. PREPARAR
        val equiposMock = listOf(
            crearEquipoMock(id = 1, codigo = "PC-01", tipo = "PC", labNombre = "Lab Redes"),
            crearEquipoMock(id = 2, codigo = "PC-02", tipo = "PC", labNombre = "Lab Software")
        )
        coEvery { mockRepository.obtenerEquiposActivos() } returns Result.success(equiposMock)

        viewModel.cargarEquipos()
        advanceUntilIdle()

        // 2. EJECUTAR: Filtrar por "Todos"
        viewModel.filtrarPorLaboratorio("Todos")

        // 3. VERIFICAR: Debe mostrar todos
        assertEquals(2, viewModel.equipos.value.size)
    }

    @Test
    fun `clearMensaje debe limpiar el mensaje`() = runTest {
        // 1. PREPARAR: Generar un error para que haya mensaje
        coEvery { mockRepository.obtenerEquiposActivos() } returns Result.failure(
            Exception("Error")
        )

        viewModel.cargarEquipos()
        advanceUntilIdle()

        // Verificar que hay mensaje
        assertNotNull(viewModel.mensaje.value)

        // 2. EJECUTAR
        viewModel.clearMensaje()

        // 3. VERIFICAR
        assertEquals(null, viewModel.mensaje.value)
    }
}
