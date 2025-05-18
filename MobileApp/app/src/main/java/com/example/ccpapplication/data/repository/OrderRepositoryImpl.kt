package com.example.ccpapplication.data.repository
import android.util.Log
import com.example.ccpapplication.data.model.Order
import com.example.ccpapplication.services.CcpApiServiceAdapter
import com.example.ccpapplication.services.interceptors.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(
    private val cppApiService: CcpApiServiceAdapter,
    private val tokenManager: TokenManager
) : OrderRepository {

    override suspend fun getOrders(tendero_id : String): List<Order> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("OrderRepository", "Iniciando petición para obtener pedidos del tendero")
                val token = tokenManager.getToken()
                Log.d("OrderRepository", "Token disponible: ${!token.isNullOrEmpty()}")

                val response = cppApiService.getOrdersTendero(tendero_id)
                Log.d("OrderRepository", "Respuesta recibida: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val orders = response.body()!!
                    Log.d("OrderRepository", "Pedidos obtenidos: ${orders.size}")
                    orders
                } else {
                    Log.e("OrderRepository", "Error al obtener pedidos: ${response.code()} - ${response.message()}")
                    if (response.errorBody() != null) {
                        try {
                            val errorBodyString = response.errorBody()?.string() ?: "No error body"
                            Log.e("OrderRepository", "Error body: $errorBodyString")
                        } catch (e: Exception) {
                            Log.e("OrderRepository", "No se pudo leer el error body")
                        }
                    }
                    emptyList()
                }


            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("OrderRepository", "Excepción al obtener pedidos: ${e.message}")
                emptyList()
            }
        }
    }

}
