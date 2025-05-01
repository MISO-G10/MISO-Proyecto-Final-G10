package com.example.ccpapplication.data.repository

import android.util.Log
import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.services.CcpApiServiceAdapter
import com.example.ccpapplication.services.interceptors.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClientRepositoryImpl(
    private val cppApiService: CcpApiServiceAdapter,
    private val tokenManager: TokenManager
) : ClientRepository {
    
    override suspend fun getClients(): List<Client> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ClientRepository", "Iniciando petición para obtener tenderos asignados")
                val token = tokenManager.getToken()
                Log.d("ClientRepository", "Token disponible: ${!token.isNullOrEmpty()}")
                
                val response = cppApiService.getAssignedClients()
                Log.d("ClientRepository", "Respuesta recibida: ${response.code()}")
                
                if (response.isSuccessful && response.body() != null) {
                    val clients = response.body()!!
                    Log.d("ClientRepository", "Tenderos obtenidos: ${clients.size}")
                    clients.forEach { client ->
                        Log.d("ClientRepository", "Tendero: ${client.name}, ID: ${client.id}")
                    }
                    clients
                } else {
                    Log.e("ClientRepository", "Error al obtener tenderos: ${response.code()} - ${response.message()}")
                    if (response.errorBody() != null) {
                        try {
                            val errorBodyString = response.errorBody()?.string() ?: "No error body"
                            Log.e("ClientRepository", "Error body: $errorBodyString")
                        } catch (e: Exception) {
                            Log.e("ClientRepository", "No se pudo leer el error body")
                        }
                    }
                    emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ClientRepository", "Excepción al obtener tenderos: ${e.message}")
                emptyList()
            }
        }
    }
}