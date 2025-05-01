package com.example.ccpapplication.data.repository

import android.util.Log
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.services.CcpApiServiceAdapter


class InventaryRepositoryImpl(
    private val cppApiService: CcpApiServiceAdapter
): InventaryRepository {
    override suspend fun getProductos(): Result<List<Producto>> {
        return try {
            // Realizamos la llamada a la API para obtener los productos
            val response = cppApiService.listProductos()
            Log.d("InventaryRepository", "Response received: ${response.code()}")

            if (response.isSuccessful) {
                // Si la respuesta es exitosa, obtenemos el cuerpo (lista de productos)
                response.body()?.let { productos ->
                    Result.success(productos)
                } ?: run {
                    Log.e("InventaryRepository", "Product data not found in response body")
                    Result.failure(Exception("Product data not found"))
                }
            } else {
                // Si la respuesta no es exitosa, registramos el error HTTP
                Log.e("InventaryRepository", "Failed with HTTP ${response.code()} - ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            // Si ocurre una excepción, la registramos y la devolvemos como un fallo
            Log.e("InventaryRepository", "Exception during product fetch: ${e.message}", e)
            Result.failure(Exception("Failed to fetch products: ${e.message}"))
        }
    }
}


