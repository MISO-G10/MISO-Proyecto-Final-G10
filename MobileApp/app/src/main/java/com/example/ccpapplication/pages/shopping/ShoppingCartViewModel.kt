package com.example.ccpapplication.pages.shopping

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ccpapplication.data.model.CartItem
import com.example.ccpapplication.data.model.Producto
import kotlinx.serialization.builtins.ListSerializer
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ccpapplication.App
import com.example.ccpapplication.data.model.PedidoRequest
import com.example.ccpapplication.data.model.ProductoPedido
import com.example.ccpapplication.data.repository.InventaryRepository
import com.example.ccpapplication.navigation.state.DataUiState
import com.example.ccpapplication.util.UiText
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.example.ccpapplication.R
import com.example.ccpapplication.data.model.User

class ShoppingCartViewModel(private val userId: String, context: Context,
                            private val inventaryRepository: InventaryRepository) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("cart_$userId", Context.MODE_PRIVATE)

    private var _cart = mutableStateOf<List<CartItem>>(emptyList())
    val cart: State<List<CartItem>> = _cart

    var pedidoUiState: DataUiState<Unit> by mutableStateOf(DataUiState.Loading)
        private set
    private val _messageEvent = MutableSharedFlow<UiText>()
    val messageEvent = _messageEvent.asSharedFlow()

    init {
        loadCartFromPrefs()
        pedidoUiState = DataUiState.Success(Unit)
    }

    fun addToCart(producto: Producto, cantidad: Int):Boolean {
        val existing = _cart.value.toMutableList()
        val index = existing.indexOfFirst { it.producto.id == producto.id }

        if (index != -1) {
            val current = existing[index]
            val nuevaCantidad = current.cantidad + cantidad

            return if (nuevaCantidad <= producto.cantidadTotal) {
                existing[index] = current.copy(cantidad = nuevaCantidad)
                _cart.value = existing
                saveCartToPrefs()
                true
            } else {
                false // no se puede añadir más que el stock disponible
            }
        } else {
            return if (cantidad <= producto.cantidadTotal) {
                existing.add(CartItem(producto, cantidad))
                _cart.value = existing
                saveCartToPrefs()
                true
            } else {
                false
            }
        }

        _cart.value = existing
        saveCartToPrefs()
    }

    fun clearCart() {
        _cart.value = emptyList()
        sharedPrefs.edit().clear().apply()
    }

    private fun saveCartToPrefs() {
        val json = kotlinx.serialization.json.Json.encodeToString(
            ListSerializer(CartItem.serializer()), _cart.value
        )
        sharedPrefs.edit().putString("cart_data", json).apply()
    }

    private fun loadCartFromPrefs() {
        val json = sharedPrefs.getString("cart_data", null)
        if (json != null) {
            val list = kotlinx.serialization.json.Json.decodeFromString(
                ListSerializer(CartItem.serializer()), json
            )
            _cart.value = list
        }
    }
    fun couldAddProduct(producto: Producto): Boolean {
        val existente = _cart.value.find { it.producto.id == producto.id }
        val cantidadActual = existente?.cantidad ?: 0
        return cantidadActual < producto.cantidadTotal
    }

    fun removeFromCart(producto: Producto, cantidad: Int = 1) {
        val existing = _cart.value.toMutableList()
        val index = existing.indexOfFirst { it.producto.id == producto.id }

        if (index != -1) {
            val currentItem = existing[index]
            val nuevaCantidad = currentItem.cantidad - cantidad

            if (nuevaCantidad > 0) {
                // Actualiza la cantidad
                existing[index] = currentItem.copy(cantidad = nuevaCantidad)
            } else {
                // Quita el producto del carrito
                existing.removeAt(index)
            }

            _cart.value = existing
            saveCartToPrefs()
        }
    }

    fun enviarPedido(user: User, productos: List<ProductoPedido>) {
        viewModelScope.launch {
            pedidoUiState = DataUiState.Loading

            val pedidoRequest = PedidoRequest(
                usuario_id = user.id,
                productos = productos,
                direccion= user.direccion.toString()
            )

            val resultado = try {
                inventaryRepository.createProducto(pedidoRequest)
            } catch (e: Exception) {
                null
            }

            if (resultado?.isSuccess == true) {
                val response = resultado.getOrNull()
                Log.d("ShoppingCartViewModel", "Pedido creado con éxito: $response")
                pedidoUiState = DataUiState.Success(Unit)

                _messageEvent.emit(UiText.StringResource(R.string.pedido_exitoso))
                clearCart()
            } else {
                val error = resultado?.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("ShoppingCartViewModel", "Error al crear pedido: $error")
                pedidoUiState = DataUiState.Error

                _messageEvent.emit(
                    UiText.StringResource(
                        R.string.error_crear_pedido,
                        error
                    )
                )
            }
        }
    }





    companion object {
        fun provideFactory(
            userId: String,
            context: Context
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                val inventaryRepository = application.container.inventarioRepository
                ShoppingCartViewModel(
                    userId = userId,
                    context = context.applicationContext,
                    inventaryRepository = inventaryRepository
                )
            }
        }
    }
}
