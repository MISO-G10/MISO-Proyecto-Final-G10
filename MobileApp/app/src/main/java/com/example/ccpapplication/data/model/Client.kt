package com.example.ccpapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Client(
    @SerialName("id") val id: String,
    @SerialName("nombre") val name: String,
    @SerialName("telefono") val telephone: String = "Sin teléfono",
    @SerialName("direccion") val address: String = "Sin dirección",
    @SerialName("username") val email: String
)