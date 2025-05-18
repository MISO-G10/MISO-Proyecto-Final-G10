package com.example.ccpapplication.data.repository
import com.example.ccpapplication.data.model.Order

interface OrderRepository {
    suspend fun getOrders(tendero_id : String): List<Order>
}