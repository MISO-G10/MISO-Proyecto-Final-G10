package com.example.ccpapplication.pages.clients

import android.util.Log
import com.example.ccpapplication.data.model.Order
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ccpapplication.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.ccpapplication.data.repository.OrderRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

open class OrderViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            try {
                // Obtener los pedidos del repositorio
                val ordersList = orderRepository.getOrders()
                _orders.value = ordersList
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    // Método protegido para manejar errores, que puede ser sobrescrito en pruebas
    protected open fun handleError(e: Exception) {
        Log.e("OrderViewModel", "Error al cargar pedidos: ${e.message}")
        e.printStackTrace()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                val orderRepository = application.container.orderRepository
                OrderViewModel(orderRepository)
            }
        }
    }
}