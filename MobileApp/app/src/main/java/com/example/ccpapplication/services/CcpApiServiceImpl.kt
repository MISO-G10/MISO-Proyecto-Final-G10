package com.example.ccpapplication.services

import com.example.ccpapplication.data.model.User
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CcpApiServiceImpl:CcpApiServiceAdapter {
    @POST("/users/login")
    suspend fun login(): User

}