package com.example.ccpapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductInOrder(
    @SerialName("id") val id: String,
    @SerialName("sku") val sku: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("perecedero") val perecedero: Boolean,
    @SerialName("fechaVencimiento") val fechaVencimiento: String?,
    @SerialName("valorUnidad") val valorUnidad: Double,
    @SerialName("tiempoEntrega") val tiempoEntrega: String,
    @SerialName("condicionAlmacenamiento") val condicionAlmacenamiento: String,
    @SerialName("reglasLegales") val reglasLegales: String,
    @SerialName("reglasComerciales") val reglasComerciales: String,
    @SerialName("reglasTributarias") val reglasTributarias: String,
    @SerialName("categoria") val categoria: Categoria,
    @SerialName("fabricante_id") val fabricanteId: String,
    @SerialName("createdAt") val createdAt: String?,
    @SerialName("updatedAt") val updatedAt: String?
)
