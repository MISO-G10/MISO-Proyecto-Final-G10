package com.example.ccpapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Client(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("telephone") val telephone: String,
    @SerialName("address") val address: String,
    @SerialName("username") val username: String
    )