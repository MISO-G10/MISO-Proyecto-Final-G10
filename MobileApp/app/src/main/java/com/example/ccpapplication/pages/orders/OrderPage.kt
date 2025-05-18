package com.example.ccpapplication.pages.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ccpapplication.R
import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.data.model.Order
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.navigation.state.DataUiState
import com.example.ccpapplication.pages.clients.ClientItem
import com.example.ccpapplication.pages.clients.OrderViewModel
import com.example.ccpapplication.pages.products.ProductCardWithDialog
import com.example.ccpapplication.pages.shopping.ShoppingCartViewModel
import com.example.ccpapplication.ui.components.DataFetchStates
import com.example.ccpapplication.ui.components.EmptyItemsScreen


@Composable
fun OrderCard(
    order: Order,
    onViewDetail: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onViewDetail() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Pedido #${order.id.take(8)}",  // Solo tomamos los primeros 8 caracteres del ID
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                order.fechaEntrega?.let { fecha ->
                    // Extraer solo la fecha (YYYY-MM-DD)
                    val fechaCorta = fecha.split("T")[0]
                    Text(
                        text = "Fecha de entrega: $fechaCorta",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "Estado: ${order.estado}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = "Valor total: $${order.valor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onViewDetail) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "Ver detalle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun OrderPage(
    orderUiState: DataUiState<List<Order>>,
    navController: NavHostController = rememberNavController(),
    viewModel: OrderViewModel = viewModel(factory = OrderViewModel.Factory),
    userId: String,
    onViewDetailOrder: (id: String) -> Unit = {}
) {

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        DataFetchStates(
            uiState = orderUiState,
            errorMessage = R.string.loading_failed_products
        ) {
            if (orderUiState !is DataUiState.Success) return@DataFetchStates
            val ordersData = orderUiState.data

            if (ordersData.isEmpty()) {
                EmptyItemsScreen(message = R.string.orders_not_found)
                return@DataFetchStates
            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ordersData) { pedido ->
                        OrderCard(
                        order = pedido,
                        onViewDetail = {onViewDetailOrder(pedido.id) }
                    )
                }
            }
        }
    }
}








