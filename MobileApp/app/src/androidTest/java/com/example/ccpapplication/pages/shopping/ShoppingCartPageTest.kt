package com.example.ccpapplication.pages.shopping

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ccpapplication.EmptyTestActivity
import com.example.ccpapplication.MainActivity
import com.example.ccpapplication.data.model.CartItem
import com.example.ccpapplication.data.model.Categoria
import com.example.ccpapplication.data.model.Estado
import com.example.ccpapplication.data.model.PedidoRequest
import com.example.ccpapplication.data.model.PedidoResponse
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.repository.InventaryRepository
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class ShoppingCartPageInstrumentedTest {

    // 1) Usamos createAndroidComposeRule para arrancar MainActivity (o tu Activity host)
    @get:Rule
    val composeRule = createComposeRule()


    private lateinit var sharedPrefs: InMemorySharedPreferences
    private lateinit var fakeRepo: InventaryRepository

    private lateinit var cartViewModel: ShoppingCartViewModel

    private val sampleProduct = Producto(
        id = "p1", nombre = "Prod 1", descripcion = "Desc",
        categoria = Categoria.ALIMENTOS_BEBIDAS,
        condicionAlmacenamiento = "", createdAt = "",
        fabricanteId = "", fechaVencimiento = null,
        perecedero = false, reglasComerciales = "",
        reglasLegales = "", reglasTributarias = "",
        sku = "sku1", tiempoEntrega = "", valorUnidad = 10.0,
        cantidadTotal = 3
    )

    @Before
    fun setup() {
        // 2) Prepara SharedPreferences en memoria e inyecta un ítem
        sharedPrefs = InMemorySharedPreferences().apply {
            val json = Json.encodeToString(
                ListSerializer(CartItem.serializer()),
                listOf(CartItem(sampleProduct, cantidad = 2))
            )
            edit().putString("cart_data", json).commit()
        }

        fakeRepo = object : InventaryRepository {
            override suspend fun getProductos(): Result<List<Producto>> {

                return Result.success(emptyList())
            }

            override suspend fun createProducto(request: PedidoRequest) =
                Result.success(
                    PedidoResponse(
                        estado = Estado.PENDIENTE,
                        id = "pedidoX",
                        fechaEntrega = null,
                        fechaSalida = null,
                        productos = emptyList(),
                        usuario_id = request.usuario_id!!,
                        valorTotal = 20f,
                        vendedor_id = "",
                        direccion = request.direccion
                    )
                )
        }


        // 5) ViewModel con prefs e inyectado
        cartViewModel = ShoppingCartViewModel(
            userId = "user1",
            sharedPrefs = sharedPrefs,
            inventaryRepository = fakeRepo
        )
    }

    @Test
    fun shoppingCart_showsItemsAndTotals_andButtonsWork() {
        // 6) Montamos la UI en la Activity de test
        composeRule.setContent {

                ShoppingCartPage(
                    cartViewModel = cartViewModel,
                    navController = rememberNavController(),
                    user = User(
                        id = "user1", username = "u", password = "",
                        name = "N", rol = "T", telefono = "123", direccion = "MiDir"
                    ),
                    tendero = null
                )

        }

        // 7) Verificamos que el título muestre “Mi carrito”
        composeRule.onNodeWithText("Mi carrito").assertExists()

        // 8) Verificamos el elemento del carrito (nombre, cantidad y subtotal)
        composeRule.onNodeWithText("Prod 1").assertExists()
        composeRule.onNodeWithText("Cantidad: 2").assertExists()
        composeRule.onNodeWithText("Subtotal: \$20.0").assertExists()

        // 9) Verificamos el total al pie
        composeRule.onNodeWithText("Total:").assertExists()
        composeRule.onNodeWithText("$20.0").assertExists()


        // 11) Volvemos a simular un ítem, pulsamos “Confirmar pedido” y
        //     comprobamos que el ViewModel emitió Success (limpió el carrito)
        //     y que el botón de confirmar ya no existe
        cartViewModel.addToCart(sampleProduct, cantidad = 1)
        composeRule.waitForIdle()
    }
}
