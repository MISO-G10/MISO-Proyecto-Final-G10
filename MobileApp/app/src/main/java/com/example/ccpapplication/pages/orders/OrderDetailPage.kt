package com.example.ccpapplication.pages.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ccpapplication.data.model.*
import java.text.NumberFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ccpapplication.pages.clients.OrderViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailPage(
    orderId: String,
    viewModel: OrderViewModel = viewModel(factory = OrderViewModel.Factory),
    navController: NavHostController

) {
    val orders by viewModel.orders.collectAsState()
    val order = orders.find { it.id == orderId }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orden #${orderId.take(8)}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Estado y fechas
                item {
                    OrderStatusSection(
                        status = order.estado,
                        creationDate = order.createdAt?.split("T")?.get(0) ?: "",
                        estimatedDeliveryDate = order.fechaEntrega?.split("T")?.get(0) ?: ""
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Título de productos
                    Text(
                        text = "Productos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Lista de productos
                items(order.orderProducts ?: emptyList()) { orderProduct ->
                    OrderProductItem(
                        orderProduct = orderProduct,
                        onFavoriteClick = { /* TODO: Implementar marcar como favorito */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Costos
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    OrderCostSection(orderProducts = order.orderProducts ?: emptyList())
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun OrderStatusSection(
    status: OrderStatus,
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
                    tint = when(status) {
                        OrderStatus.PENDIENTE -> Color.Yellow
                        OrderStatus.ENVIADO -> Color.Blue
                        OrderStatus.ENTREGADO -> Color.Green
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when(status) {
                        OrderStatus.PENDIENTE -> "Pendiente"
                        OrderStatus.ENVIADO -> "Enviado"
                        OrderStatus.ENTREGADO -> "Entregado"
                    },
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
    orderProduct: OrderProduct,
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
                    text = orderProduct.producto?.nombre ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = orderProduct.producto?.categoria?.name ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Cantidad: ${orderProduct.cantidad}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatCurrency(orderProduct.valorUnitario.toDouble() * orderProduct.cantidad.toDouble()),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { onFavoriteClick(orderProduct.producto?.sku ?: "") }) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "Marcar como favorito"
                )
            }
        }
    }
}

@Composable
fun OrderCostSection(orderProducts: List<OrderProduct>) {
    val subtotal = orderProducts.sumOf { product: OrderProduct -> 
        product.valorUnitario.toDouble() * product.cantidad.toDouble()
    }
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