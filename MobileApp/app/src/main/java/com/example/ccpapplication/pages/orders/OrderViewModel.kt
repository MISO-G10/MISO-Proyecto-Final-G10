package com.example.ccpapplication.pages.clients

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ccpapplication.data.model.Order
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ccpapplication.App
import com.example.ccpapplication.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.ccpapplication.data.repository.OrderRepository
import com.example.ccpapplication.navigation.state.DataUiState
import com.example.ccpapplication.services.interceptors.TokenManager


open class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    var orderUiState: DataUiState<List<Order>> by mutableStateOf(DataUiState.Loading)
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    init {
        loadOrders()
    }

    fun loadOrders() {
        orderUiState = DataUiState.Loading
        viewModelScope.launch {
            try {
                val user = tokenManager.getUser()
                val userId = user?.id
                // Obtener los pedidos del repositorio para el tendero actual
                val ordersList = orderRepository.getOrders(userId ?: "")
                _orders.value = ordersList
                orderUiState = DataUiState.Success(ordersList)
            } catch (e: Exception) {
                handleError(e)
                orderUiState = DataUiState.Error
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
                val tokenManager = application.container.tokenManager
                OrderViewModel(orderRepository = orderRepository, tokenManager = tokenManager)
            }
        }
    }
}