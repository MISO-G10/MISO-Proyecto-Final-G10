package com.example.ccpapplication.pages.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ccpapplication.data.model.Producto
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailPage(
    orderId: String,
    products: List<Producto>,
    onBackClick: () -> Unit,
    onFavoriteClick: (String) -> Unit,
    orderStatus: String = "Recibido",
    creationDate: String = "28/02/2025",
    estimatedDeliveryDate: String = "01/03/2025"
) {
    val showMenu = remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orden #$orderId") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu.value = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                    }
                    DropdownMenu(
                        expanded = showMenu.value,
                        onDismissRequest = { showMenu.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Opción 1") },
                            onClick = { showMenu.value = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Opción 2") },
                            onClick = { showMenu.value = false }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Estado y fechas
            OrderStatusSection(
                status = orderStatus,
                creationDate = creationDate,
                estimatedDeliveryDate = estimatedDeliveryDate
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Lista de productos
            Text(
                text = "Productos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de productos
            products.forEach { product ->
                OrderProductItem(
                    product = product,
                    onFavoriteClick = onFavoriteClick
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Costos
            OrderCostSection(products = products)
        }
    }
}

@Composable
fun OrderStatusSection(
    status: String,
    creationDate: String,
    estimatedDeliveryDate: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = status,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Fecha de creación",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = creationDate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Fecha estimada de entrega",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = estimatedDeliveryDate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun OrderProductItem(
    product: Producto,
    onFavoriteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.categoria,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Cantidad: ${product.cantidad ?: 1}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatCurrency(product.valorUnidad * (product.cantidad ?: 1)),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { onFavoriteClick(product.sku) }) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "Marcar como favorito"
                )
            }
        }
    }
}

@Composable
fun OrderCostSection(products: List<Producto>) {
    val subtotal = products.sumOf { it.valorUnidad * (it.cantidad ?: 1) }
    val ivaPercentage = 0.19
    val iva = subtotal * ivaPercentage
    val total = subtotal + iva

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumen de costos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal")
                Text(formatCurrency(subtotal))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("IVA (19%)")
                Text(formatCurrency(iva))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total a pagar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    formatCurrency(total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    return format.format(amount)
}
