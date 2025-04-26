package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.Client

class ClientRepositoryImpl : ClientRepository {
    override suspend fun getClients(): List<Client> {
        // En lugar de este mock, llamar al API
        return listOf(
            Client("Tienda Don Jose", "Jose Vaquero", "Calle 53 #123"),
            Client("Tienda Don Pepe", "Pepe Mujica", "Calle 24 # 127-15"),
            Client("Tienda Calleja", "Pablo Garcia", "Calle 127 # 127-15"),
            Client("Tienda El Restrepo", "Julieth Cano", "Calle 13 sur # 127-15"),
        )
    }
}