package com.example.ccpapplication.data.model

@kotlinx.serialization.Serializable
data class CartItem (
    val producto: Producto,
    val cantidad: Int
)