package com.example.ccpapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    @SerialName("id") val id: String,
    @SerialName("categoria") val categoria: Categoria,
    @SerialName("condicionAlmacenamiento") val condicionAlmacenamiento: String,
    @SerialName("createdAt") val createdAt: String, // Puedes usar String o un tipo especial si parseas fechas
    @SerialName("descripcion") val descripcion: String,
    @SerialName("fabricante_id") val fabricanteId: String,
    @SerialName("fechaVencimiento") val fechaVencimiento: String?,
    @SerialName("nombre") val nombre: String,
    @SerialName("perecedero") val perecedero: Boolean,
    @SerialName("reglasComerciales") val reglasComerciales: String,
    @SerialName("reglasLegales") val reglasLegales: String,
    @SerialName("reglasTributarias") val reglasTributarias: String,
    @SerialName("sku") val sku: String,
    @SerialName("tiempoEntrega") val tiempoEntrega: String,
    @SerialName("valorUnidad") val valorUnidad: Float,
    @SerialName("cantidad_total") val cantidadTotal: Int
)

@Serializable
enum class Categoria {
    @SerialName("ALIMENTOS_BEBIDAS")
    ALIMENTOS_BEBIDAS,

    @SerialName("CUIDADO_PERSONAL")
    CUIDADO_PERSONAL,

    @SerialName("LIMPIEZA_HOGAR")
    LIMPIEZA_HOGAR,

    @SerialName("BEBES")
    BEBES,

    @SerialName("MASCOTAS")
    MASCOTAS
}
