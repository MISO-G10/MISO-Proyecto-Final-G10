package com.example.ccpapplication.services

import com.example.ccpapplication.data.model.AddVisitResponse
import com.example.ccpapplication.data.model.AuthResponse
import com.example.ccpapplication.data.model.UserRegistrationResponse
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.model.UserLogin
import com.example.ccpapplication.data.model.UserRegistration
import retrofit2.Response


interface CcpApiServiceAdapter {
    suspend fun getUser(): Response<User>
    suspend fun login(userLogin: UserLogin): Response<AuthResponse>
    suspend fun addVisit(): Response<AddVisitResponse>
    suspend fun registerUser(user: UserRegistration): Response<UserRegistrationResponse>
}