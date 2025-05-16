package com.example.ccpapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductoPedido(
        @SerialName("producto_id") val producto_id:String,
        @SerialName("cantidad") val cantidad:Int
)
@Serializable
data class ProductoPedidoResponse(
    @SerialName("cantidad") val cantidad:Float,
    @SerialName("producto_id") val producto_id:String,
    @SerialName("subtotal") val subtotal:Float,
    @SerialName("valorUnitario") val valorUnitario:Float,
)

@Serializable
data class PedidoRequest(
    @SerialName("usuario_id") val usuario_id:String?,
    @SerialName("productos") val productos : List<ProductoPedido>
)

@Serializable
data class PedidoResponse(
    @SerialName("estado") val producto_id:Estado,
    @SerialName("id") val id:String,
    @SerialName("fechaEntrega") val fechaEntrega:String?,
    @SerialName("fechaSalida") val fechaSalida:String?,
    @SerialName("productos") val productos:List<ProductoPedidoResponse>,
    @SerialName("usuario_id") val usuario_id:String,
    @SerialName("valorTotal") val valorTotal:Float,
    @SerialName("vendedor_id") val vendedor_id:String,
)

@Serializable
enum class Estado {
    @SerialName("PENDIENTE")
    PENDIENTE,

    @SerialName("ENVIADO")
    ENVIADO,

    @SerialName("ENTREGADO")
    ENTREGADO
}
