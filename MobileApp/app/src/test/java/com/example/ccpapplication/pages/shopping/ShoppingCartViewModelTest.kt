package com.example.ccpapplication.pages.shopping

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.example.ccpapplication.data.model.Categoria
import com.example.ccpapplication.data.model.Estado
import com.example.ccpapplication.data.model.PedidoRequest
import com.example.ccpapplication.data.model.PedidoResponse
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.data.model.ProductoPedido
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.repository.InventaryRepository
import com.example.ccpapplication.navigation.state.DataUiState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingCartViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var sharedPrefs: InMemorySharedPreferences
    private lateinit var fakeRepo: InventaryRepository
    private lateinit var vm: ShoppingCartViewModel

    // Muestra un producto de prueba
    private val sampleProduct = Producto(
        id = "prod1",
        nombre = "Producto 1",
        descripcion = "Desc",
        categoria = Categoria.ALIMENTOS_BEBIDAS,
        condicionAlmacenamiento = "Seco",
        createdAt = "",
        fabricanteId = "fab1",
        fechaVencimiento = null,
        perecedero = false,
        reglasComerciales = "",
        reglasLegales = "",
        reglasTributarias = "",
        sku = "sku1",
        tiempoEntrega = "",
        valorUnidad = 10.0,
        cantidadTotal = 5
    )

    @Before
    fun setup() {
        // 1) Intercepta Main dispatcher
        Dispatchers.setMain(testDispatcher)

        // 2) Crea un Context de test y un SharedPreferences "en memoria"
        sharedPrefs = InMemorySharedPreferences()


        // 3) Fake repo que solo se usa en tests de enviarPedido()
        fakeRepo = object : InventaryRepository {
            override suspend fun getProductos() = throw NotImplementedError()
            override suspend fun createProducto(request: PedidoRequest) = Result.success(PedidoResponse(
                estado = Estado.PENDIENTE,
                id = "pedido1",
                fechaEntrega = null,
                fechaSalida = null,
                productos = emptyList(),
                usuario_id = request.usuario_id!!,
                valorTotal = 0f,
                vendedor_id = "",
                direccion = request.direccion,

            ))
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(overrideUser: String? = null) {
        vm = ShoppingCartViewModel(
            userId = "user1",
            sharedPrefs = sharedPrefs,
            inventaryRepository = fakeRepo,
            overrideUserId = overrideUser
        )
    }

    @Test
    fun `init loads empty cart and sets Success state`() = runTest {
        buildViewModel()
        // init() llama a loadCartFromPrefs() y luego pone pedidoUiState = Success
        assertTrue(vm.cart.value.isEmpty())
        assertTrue(vm.pedidoUiState is DataUiState.Success)
    }

    @Test
    fun `addToCart agrega nuevo item si no existía`() = runTest {
        buildViewModel()
        val added = vm.addToCart(sampleProduct, cantidad = 2)
        assertTrue(added)
        assertEquals(1, vm.cart.value.size)
        assertEquals(2, vm.cart.value.first().cantidad)
    }

    @Test
    fun `addToCart acumula cantidad si ya existe y no supera stock`() = runTest {
        buildViewModel()
        vm.addToCart(sampleProduct, 2)
        val added = vm.addToCart(sampleProduct, 3)  // total = 5 igual stock
        assertTrue(added)
        assertEquals(1, vm.cart.value.size)
        assertEquals(5, vm.cart.value.first().cantidad)
    }

    @Test
    fun `addToCart devuelve false si excede stock`() = runTest {
        buildViewModel()
        val added = vm.addToCart(sampleProduct, cantidad = 6)
        assertFalse(added)
        assertTrue(vm.cart.value.isEmpty())
    }

    @Test
    fun `couldAddProduct detecta límite de stock`() = runTest {
        buildViewModel()
        vm.addToCart(sampleProduct, 5)
        assertFalse(vm.couldAddProduct(sampleProduct))
    }

    @Test
    fun `removeFromCart decrementa cantidad y remueve si llega a cero`() = runTest {
        buildViewModel()
        vm.addToCart(sampleProduct, 3)
        vm.removeFromCart(sampleProduct, 2)
        assertEquals(1, vm.cart.value.first().cantidad)

        vm.removeFromCart(sampleProduct, 1)
        assertTrue(vm.cart.value.isEmpty())
    }

    @Test
    fun `clearCart vacía carrito y SharedPreferences`() = runTest {
        buildViewModel()
        vm.addToCart(sampleProduct, 1)
        vm.clearCart()
        assertTrue(vm.cart.value.isEmpty())

        // Verificamos que SharedPreferences ya no tenga datos
        val stored = sharedPrefs.getString("cart_data", null)
        assertNull(stored)
    }

    @Test
    fun `enviarPedido emite Loading y Success y limpia carrito`() = runTest {
        buildViewModel()
        vm.addToCart(sampleProduct, 1)

        // Observamos los cambios de estado en pedidoUiState
        vm.enviarPedido(user = User(
            id = "user1", direccion = "Dir",
            username = "username",
            password = "passowrd",
            name = "name",
            rol = "TENDERO",
            telefono = "123456"
        ), productos = listOf(
            ProductoPedido(producto_id = sampleProduct.id, cantidad = 1)
        ))

        // Ejecutamos coroutines
        testScheduler.advanceUntilIdle()

        assertTrue(vm.pedidoUiState is DataUiState.Success)
        assertTrue(vm.cart.value.isEmpty())
    }
}

class InMemorySharedPreferences : SharedPreferences {
    private val map = mutableMapOf<String, String>()
    override fun getString(key: String, defValue: String?) = map[key] ?: defValue
    override fun edit() = object : SharedPreferences.Editor {
        override fun putString(key: String, value: String?) = apply {
            if (value != null) map[key] = value
        }
        override fun clear() = apply { map.clear() }
        override fun apply() = Unit
        override fun commit() = true
        // resto de métodos los puedes dejar no soportados o vacíos si no los usas
        override fun remove(key: String) = apply { map.remove(key) }
        override fun putStringSet(key: String, values: MutableSet<String>?) = throw UnsupportedOperationException()
        override fun putInt(key: String, value: Int) = throw UnsupportedOperationException()
        override fun putLong(key: String, value: Long) = throw UnsupportedOperationException()
        override fun putFloat(key: String, value: Float) = throw UnsupportedOperationException()
        override fun putBoolean(key: String, value: Boolean) = throw UnsupportedOperationException()
    }
    // implementa los getters que necesites (getInt, getBoolean, etc.) o lanza UnsupportedOperation
    override fun contains(key: String) = map.containsKey(key)
    override fun getAll() = map.toMap()
    override fun getInt(key: String, defValue: Int) = throw UnsupportedOperationException()
    override fun getLong(key: String, defValue: Long) = throw UnsupportedOperationException()
    override fun getFloat(key: String, defValue: Float) = throw UnsupportedOperationException()
    override fun getBoolean(key: String, defValue: Boolean) = throw UnsupportedOperationException()
    override fun getStringSet(key: String, defValues: MutableSet<String>?) = throw UnsupportedOperationException()
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {}
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {}
}
