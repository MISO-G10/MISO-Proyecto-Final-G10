package com.example.ccpapplication.pages.shopping

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ccpapplication.data.model.CartItem
import com.example.ccpapplication.data.model.Producto
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import androidx.compose.runtime.State

class ShoppingCartViewModel(private val userId: String, context: Context) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("cart_$userId", Context.MODE_PRIVATE)

    private var _cart = mutableStateOf<List<CartItem>>(emptyList())
    val cart: State<List<CartItem>> = _cart

    init {
        loadCartFromPrefs()
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



    companion object {
        fun provideFactory(userId: String, context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ShoppingCartViewModel(userId, context.applicationContext) as T
            }
        }
    }
}
