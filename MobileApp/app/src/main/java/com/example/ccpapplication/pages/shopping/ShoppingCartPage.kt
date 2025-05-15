package com.example.ccpapplication.pages.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.ccpapplication.data.model.CartItem

@Composable
fun ShoppingCartPage(cartViewModel: ShoppingCartViewModel,navController : NavController,) {
    val cart = cartViewModel.cart.value
    var itemToRemove by remember { mutableStateOf<CartItem?>(null) }
    val total = cart.sumOf { it.cantidad * it.producto.valorUnidad.toDouble() }

    itemToRemove?.let { item ->
        RemoveFromCartDialog(
            product = item.producto,
            currentQuantity = item.cantidad,
            onDismiss = { itemToRemove = null },
            onRemove = { cantidad ->
                cartViewModel.removeFromCart(item.producto, cantidad)
            }
        )
    }

    Column(Modifier.padding(16.dp)) {
        Text("Mi carrito", style = MaterialTheme.typography.headlineSmall)

        if (cart.isNotEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(cart) { item ->
                    CartItemCard(
                        item = item,
                        onRemove = { itemToRemove = item }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            TotalCard(total = total)
            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                cartViewModel.clearCart()
                // Aquí podrías lanzar una petición real si es necesario
            }) {
                Text("Confirmar pedido")
            }
        } else {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Aún no tienes nada en el carrito.", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(onClick = {
                navController.navigate("catalog")
            }) {
                Text("Ir al catálogo")
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onRemove: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.producto.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Cantidad: ${item.cantidad}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Subtotal: \$${item.cantidad * item.producto.valorUnidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar del carrito",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun TotalCard(total: Double) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
