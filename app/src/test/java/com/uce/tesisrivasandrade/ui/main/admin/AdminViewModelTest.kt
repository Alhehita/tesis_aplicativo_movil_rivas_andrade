package com.uce.tesisrivasandrade.ui.main.admin

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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AdminViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AdminViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al iniciar carga isLoading debe ser true`() = runTest(testDispatcher) {
        viewModel.cargarDatos("Test")

        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertNull(state.mensaje)
        assertNull(state.error)
    }

    @Test
    fun `cargarDatos con username debe mostrar mensaje de bienvenida personalizado`() = runTest(testDispatcher) {
        viewModel.cargarDatos("Juan")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Bienvenido, Juan 🎓", state.mensaje)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `cargarDatos sin username debe mostrar mensaje generico`() = runTest(testDispatcher) {
        viewModel.cargarDatos(null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Bienvenido Admin 🎓", state.mensaje)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `despues de cargar isLoading vuelve a false`() = runTest(testDispatcher) {
        viewModel.cargarDatos("Maria")

        // Antes del delay debe estar cargando
        assertTrue(viewModel.uiState.value.isLoading)

        // Después del delay debe terminar
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.mensaje)
    }
}
