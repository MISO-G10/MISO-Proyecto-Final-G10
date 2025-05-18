package com.example.ccpapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    @SerialName("id") val id: String,
    @SerialName("vendedor_id") val vendedorId: String,
    @SerialName("usuario_id") val usuarioId: String?,
    @SerialName("fechaEntrega") val fechaEntrega: String?,
    @SerialName("fechaSalida") val fechaSalida: String?,
    @SerialName("estado") val estado: OrderStatus,
    @SerialName("valor") val valor: Float,
    @SerialName("direccion") val direccion: String,
    @SerialName("pedido_productos") val orderProducts: List<OrderProduct>?
)

@Serializable
data class OrderProduct(
    @SerialName("cantidad") val cantidad: Float,
    @SerialName("pedido_id") val pedidoId: String,
    @SerialName("producto_id") val productoId: String,
    @SerialName("valorUnitario") val valorUnitario: Float,
    @SerialName("subtotal") val subtotal: Float,
    @SerialName("producto") val producto: Producto?
)

@Serializable
enum class OrderStatus {
    @SerialName("PENDIENTE")
    PENDIENTE,

    @SerialName("ENVIADO")
    ENVIADO,

    @SerialName("ENTREGADO")
    ENTREGADO
}
