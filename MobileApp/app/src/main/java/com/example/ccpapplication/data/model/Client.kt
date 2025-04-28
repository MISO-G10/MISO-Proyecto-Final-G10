package com.example.ccpapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Client(
    @SerialName("id") val id: String,
    @SerialName("nombre") val name: String,
    @SerialName("telefono") val telephone: String = "Sin teléfono",
    @SerialName("direccion") val address: String = "Sin dirección",
    @SerialName("username") val email: String,
    @SerialName("apellido") val lastName: String = "",
    @SerialName("ultima_visita") val lastVisitDate: String? = null,
    @SerialName("numero_visitas") val visitCount: Int = 0
)