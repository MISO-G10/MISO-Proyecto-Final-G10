package com.example.ccpapplication.services

import com.example.ccpapplication.data.model.AddVisitResponse
import com.example.ccpapplication.data.model.AuthResponse
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.model.UserLogin
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET

interface CcpApiServiceImpl:CcpApiServiceAdapter {
    @GET("me")
    override suspend fun getUser(): Response<User>
    @POST("auth")
    override suspend fun login(@Body userLogin: UserLogin): Response<AuthResponse>
    @POST("")
    override suspend fun addVisit(): Response<AddVisitResponse>

}