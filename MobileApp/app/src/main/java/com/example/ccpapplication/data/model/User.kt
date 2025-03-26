package com.example.ccpapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
    @SerialName("name") val name: String,
    @SerialName("rol") val rol: String,

)

@Serializable
data class UserLogin(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,


    )
