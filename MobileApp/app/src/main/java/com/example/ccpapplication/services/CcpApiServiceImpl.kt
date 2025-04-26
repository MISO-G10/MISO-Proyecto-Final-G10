package com.example.ccpapplication.services

import com.example.ccpapplication.data.model.AuthResponse
import com.example.ccpapplication.data.model.UserRegistrationResponse
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.model.UserLogin
import com.example.ccpapplication.data.model.UserRegistration
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET

interface CcpApiServiceImpl:CcpApiServiceAdapter {
    @GET("/usuarios/me")
    override suspend fun getUser(): Response<User>
    @POST("/usuarios/auth")
    override suspend fun login(@Body userLogin: UserLogin): Response<AuthResponse>
    @POST("/usuarios")
    override suspend fun registerUser(@Body user: UserRegistration): Response<UserRegistrationResponse>

}