package com.example.ccpapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("nombre") val password: String,
    @SerialName("apellido") val name: String,
    @SerialName("rol") val rol: String,
    @SerialName("direccion") val direccion: String?,
    @SerialName("telefono") val telefono: String?

)

@Serializable
data class UserLogin(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,


    )

@Serializable
data class UserRegistration(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
    @SerialName("nombre") val firstname: String,
    @SerialName("apellido") val lastname: String,
    @SerialName("rol") val rol: String
)

@Serializable
data class AuthResponse(
    @SerialName("token") val token: String,
    @SerialName("id") val id: String,
    @SerialName("expireAt") val expireAt: String
)

@Serializable
data class UserRegistrationResponse(
    @SerialName("createdAt") val createdAt: String,
    @SerialName("id") val id: String
)
