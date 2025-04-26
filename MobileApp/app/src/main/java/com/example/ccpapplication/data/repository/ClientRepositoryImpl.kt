package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.Client

class ClientRepositoryImpl : ClientRepository {
    override suspend fun getClients(): List<Client> {
        // En lugar de este mock, llamar al API
        return listOf(
            Client(id = "6947210a-5bc1-456d-aaab-e475cf3d71f7", name="Tendero Luis", telephone="3112254000", address="Calle 127 # 127-15", username = "luis@gmail.com"),
            Client(id = "1909723c-3337-494b-932e-84fb6d8dbd9c", name="Tienda Pepe", telephone="3186934007", address="Calle 127 # 127-16", username = "pepe@gmail.com"),
            Client(id = "6858f755-3674-4c44-9201-985698398a33", name="Tienda Juan", telephone="2312681", address="Calle 127 # 127-17", username = "juan@gmail.com")
        )
    }
}