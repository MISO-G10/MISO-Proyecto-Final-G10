package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.model.UserLogin

interface UserRepository {
    suspend fun getUser(userId: String): User
    suspend fun login(userLogin: UserLogin): User?

}