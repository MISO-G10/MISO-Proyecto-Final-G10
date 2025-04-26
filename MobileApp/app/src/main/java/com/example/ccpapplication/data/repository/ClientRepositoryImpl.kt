package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.Client

class ClientRepositoryImpl : ClientRepository {
    override suspend fun getClients(): List<Client> {
        // En lugar de este mock, llamar al API
        return listOf(
            Client(id = "1909823c-3337-499b-932e-84fb6d8dbd9c", "Tienda Don Jose", "Jose Vaquero", "Calle 53 #123"),
            Client(id = "2909823c-3337-490b-932e-84fb6d8dbd9c", "Tienda Don Pepe", "Pepe Mujica", "Calle 24 # 127-15"),
            Client(id = "3909823c-3337-492b-932e-84fb6d8dbd9c", "Tienda Calleja", "Pablo Garcia", "Calle 127 # 127-15"),
            Client(id = "4909823c-3337-495b-932e-84fb6d8dbd9c", "Tienda El Restrepo", "Julieth Cano", "Calle 13 sur # 127-15"),
        )
    }
}