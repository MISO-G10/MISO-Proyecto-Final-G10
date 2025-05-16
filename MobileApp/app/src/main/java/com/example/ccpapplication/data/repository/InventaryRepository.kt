package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.AddVisitResponse
import com.example.ccpapplication.data.model.PedidoRequest
import com.example.ccpapplication.data.model.PedidoResponse
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.data.model.VisitAdd

interface InventaryRepository {
    suspend fun getProductos(): Result<List<Producto>> //creación de visitas
    suspend fun createProducto(request: PedidoRequest): Result<PedidoResponse>
}