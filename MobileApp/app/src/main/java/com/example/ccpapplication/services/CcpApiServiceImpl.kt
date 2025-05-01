package com.example.ccpapplication.services

import com.example.ccpapplication.data.model.AddVisitResponse
import com.example.ccpapplication.data.model.AuthResponse
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.model.UserLogin
import com.example.ccpapplication.data.model.UserRegistration
import com.example.ccpapplication.data.model.UserRegistrationResponse
import com.example.ccpapplication.data.model.VisitAdd
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Headers

interface CcpApiServiceImpl:CcpApiServiceAdapter {
    @GET("/usuarios/me")
    override suspend fun getUser(): Response<User>
    @POST("/usuarios/auth")
    override suspend fun login(@Body userLogin: UserLogin): Response<AuthResponse>
    @POST("/visitas")
    override suspend fun addVisit(@Body visit: VisitAdd): Response<AddVisitResponse>
    @POST("/usuarios")
    override suspend fun registerUser(@Body user: UserRegistration): Response<UserRegistrationResponse>
    @GET("/inventarios/productos")
    override suspend fun listProductos():Response<List<Producto>>    
    @GET("/visitas/asignaciones/mis-tenderos")
    @Headers("Content-Type: application/json")
    override suspend fun getAssignedClients(): Response<List<Client>>
}