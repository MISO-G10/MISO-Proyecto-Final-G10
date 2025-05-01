package com.example.ccpapplication.pages.products

import com.example.ccpapplication.data.model.Categoria
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.data.repository.InventaryRepository

class FakeInventaryRepository : InventaryRepository {
    override suspend fun getProductos(): Result<List<Producto>> {
        return Result.success(
            listOf(
                Producto(
                    categoria = Categoria.ALIMENTOS_BEBIDAS,
                    condicionAlmacenamiento = "Seco",
                    createdAt = "2024-01-01T00:00:00",
                    descripcion = "Producto de prueba 1",
                    fabricanteId = "1",
                    fechaVencimiento = "2025-01-01",
                    nombre = "Producto 1",
                    perecedero = true,
                    reglasComerciales = "Reglas 1",
                    reglasLegales = "Legales 1",
                    reglasTributarias = "Tributarias 1",
                    sku = "sku1",
                    tiempoEntrega = "2 días",
                    valorUnidad = 12.5f
                ),
                Producto(
                    categoria = Categoria.LIMPIEZA_HOGAR,
                    condicionAlmacenamiento = "Fresco",
                    createdAt = "2024-01-02T00:00:00",
                    descripcion = "Producto de prueba 2",
                    fabricanteId = "2",
                    fechaVencimiento = "2026-01-01",
                    nombre = "Producto 2",
                    perecedero = false,
                    reglasComerciales = "Reglas 2",
                    reglasLegales = "Legales 2",
                    reglasTributarias = "Tributarias 2",
                    sku = "sku2",
                    tiempoEntrega = "5 días",
                    valorUnidad = 20.0f
                )
            )
        )
    }
}
