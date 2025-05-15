package com.example.ccpapplication.pages.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items

@Composable
fun ShoppingCartPage(cartViewModel: ShoppingCartViewModel) {
    val cart = cartViewModel.cart.value

    Column(Modifier.padding(16.dp)) {
        Text("Mi carrito", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cart) { item ->
                Card {
                    Column(Modifier.padding(12.dp)) {
                        Text(item.producto.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("Cantidad: ${item.cantidad}")
                        Text("Subtotal: \$${item.cantidad * item.producto.valorUnidad}")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            cartViewModel.clearCart()
            // aquí podrías lanzar una petición real si es necesario
        }) {
            Text("Confirmar pedido")
        }
    }
}
