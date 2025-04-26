package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.AuthResponse
import com.example.ccpapplication.data.model.UserRegistrationResponse
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.model.UserLogin
import com.example.ccpapplication.data.model.UserRegistration

interface UserRepository {
    suspend fun getUser(): Result<User>
    suspend fun login(userLogin: UserLogin): Result<AuthResponse>
    suspend fun registerUser(user: UserRegistration): Result<UserRegistrationResponse> //función para creación de usuarios

}