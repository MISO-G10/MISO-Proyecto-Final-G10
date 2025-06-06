package com.example.ccpapplication.pages.shopping

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.ccpapplication.R
import com.example.ccpapplication.data.model.CartItem
import com.example.ccpapplication.data.model.ProductoPedido
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.ui.components.ButtonType
import com.example.ccpapplication.ui.components.GenericButton
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ShoppingCartPage(
    cartViewModel: ShoppingCartViewModel,
    navController: NavController,
    user: User?,
    tendero: User?,
    enableLazyColumnScroll: Boolean = true
) {
    val cart = cartViewModel.cart.value
    var itemToRemove by remember { mutableStateOf<CartItem?>(null) }
    val total = cart.sumOf { it.cantidad * it.producto.valorUnidad.toDouble() }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        cartViewModel.messageEvent.collectLatest { uiText ->
            val message = uiText.asString(context)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
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
        Text(
            text = if (!tendero?.username.isNullOrBlank()) {
                "Carrito para ${tendero!!.username}"
            } else {
                "Mi carrito"
            },
            style = MaterialTheme.typography.headlineSmall
        )


        if (cart.isNotEmpty()) {
            if (enableLazyColumnScroll) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cart) { item ->
                        CartItemCard(
                            item = item,
                            onRemove = { itemToRemove = item }
                        )
                    }
                }
            }else{
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    cart.forEach { item ->
                        CartItemCard(
                            item = item,
                            onRemove = { itemToRemove = item }
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            TotalCard(total = total, user = tendero ?: user)
            Spacer(Modifier.height(16.dp))

            GenericButton(
                label = stringResource(R.string.confirm_order),
                onClick = {
                    (tendero ?: user)?.let { selectedUser ->
                        val productosPedido = cart.map {
                            ProductoPedido(
                                producto_id = it.producto.id,
                                cantidad = it.cantidad
                            )
                        }
                        cartViewModel.enviarPedido(selectedUser, productosPedido)
                    }
                },
                type = ButtonType.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            GenericButton(
                label = stringResource(R.string.clean_cart),
                onClick = {
                    cartViewModel.clearCart()
                },
                type = ButtonType.TERTIARY,
                modifier = Modifier.fillMaxWidth()
            )


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
fun TotalCard(total: Double, user: User?) {
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
                text = "Dirección de envio:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = user?.direccion.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

    }
}
