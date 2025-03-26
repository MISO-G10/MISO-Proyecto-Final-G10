package com.example.ccpapplication.services

import com.example.ccpapplication.data.model.User

interface CcpApiServiceAdapter {
    suspend fun getUser(userId: String): User
    suspend fun login(username: String, password: String): User?
}