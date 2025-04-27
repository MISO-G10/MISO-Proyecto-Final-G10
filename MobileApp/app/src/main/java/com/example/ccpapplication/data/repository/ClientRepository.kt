package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.Client

interface ClientRepository {
    suspend fun getClients(): List<Client>
}