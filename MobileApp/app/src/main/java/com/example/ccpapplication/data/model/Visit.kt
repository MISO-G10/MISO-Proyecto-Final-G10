package com.example.ccpapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Visit(
    @SerialName("id") val id: String,
    @SerialName("fecha") val date: String,
    @SerialName("horaDesde") val hourFrom: String,
    @SerialName("horaHasta") val hourTo: String,
    @SerialName("comentarios") val comments: String,
    @SerialName("idUsuario") val idUser: String
)

@Serializable
data class VisitAdd(
    @SerialName("fecha") val date: String,
    @SerialName("horaDesde") val hourFrom: String,
    @SerialName("horaHasta") val hourTo: String,
    @SerialName("comentarios") val comments: String,
    @SerialName("idUsuario") val idUser: String
)

@Serializable
data class AddVisitResponse(
    @SerialName("id") val id: String,
    @SerialName("createdAt") val createdAt: String
)