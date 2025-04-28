package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.AddVisitResponse
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.data.model.VisitAdd

interface InventaryRepository {
    suspend fun getProductos(): Result<List<Producto>> //creación de visitas
}