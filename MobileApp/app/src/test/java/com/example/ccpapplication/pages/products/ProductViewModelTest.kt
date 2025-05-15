package com.example.ccpapplication.pages.products

import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.data.repository.InventaryRepository
import com.example.ccpapplication.navigation.state.DataUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException
import com.example.ccpapplication.data.model.Categoria

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ProductViewModel
    private val sampleProduct = Producto(
        categoria = Categoria.ALIMENTOS_BEBIDAS,
        condicionAlmacenamiento = "Seco y fresco",
        createdAt = "2025-04-30T12:00:00Z",
        descripcion = "Producto de prueba",
        fabricanteId = "FAB123",
        fechaVencimiento = "2026-04-30",
        nombre = "Galletas integrales",
        perecedero = true,
        reglasComerciales = "Reglas de comercio",
        reglasLegales = "Reglas legales",
        reglasTributarias = "Reglas tributarias",
        sku = "SKU12345",
        tiempoEntrega = "3 días",
        valorUnidad = 5.99f
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchProducts should emit Success state when repository returns data`() = runTest {
        val fakeRepository = object : InventaryRepository {
            override suspend fun getProductos(): Result<List<Producto>> {
                return Result.success(listOf(sampleProduct))
            }
        }

        viewModel = ProductViewModel(fakeRepository)

        testScheduler.advanceUntilIdle()

        val state = viewModel.productUiState
        assertTrue(state is DataUiState.Success)
        val products = (state as DataUiState.Success).data
        assertEquals(1, products.size)
        assertEquals("Galletas integrales", products[0].nombre)
    }

    @Test
    fun `fetchProducts should emit Error state when repository returns failure`() = runTest {
        val fakeRepository = object : InventaryRepository {
            override suspend fun getProductos(): Result<List<Producto>> {
                return Result.failure(Exception("Error al obtener productos"))
            }
        }

        viewModel = ProductViewModel(fakeRepository)

        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.productUiState is DataUiState.Error)
    }

    @Test
    fun `fetchProducts should emit Error state when IOException is thrown`() = runTest {
        val fakeRepository = object : InventaryRepository {
            override suspend fun getProductos(): Result<List<Producto>> {
                throw IOException("No hay internet") as Throwable
            }
        }

        viewModel = ProductViewModel(fakeRepository)

        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.productUiState is DataUiState.Error)
    }

    @Test
    fun `fetchProducts should emit Error state when HttpException is thrown`() = runTest {
        val fakeRepository = object : InventaryRepository {
            override suspend fun getProductos(): Result<List<Producto>> {
                throw retrofit2.HttpException(retrofit2.Response.error<String>(500, okhttp3.ResponseBody.create(null, "Error del servidor")))
            }
        }

        viewModel = ProductViewModel(fakeRepository)

        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.productUiState is DataUiState.Error)
    }
}