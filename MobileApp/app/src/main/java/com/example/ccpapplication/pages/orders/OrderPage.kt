package com.example.ccpapplication.pages.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ccpapplication.R
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.navigation.state.DataUiState
import com.example.ccpapplication.pages.products.ProductCard
import com.example.ccpapplication.ui.components.DataFetchStates
import com.example.ccpapplication.ui.components.EmptyItemsScreen

@Composable
fun Order(
    product: Producto,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    showAddToShopping: Boolean,
    onViewDetail: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onViewDetail() }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.descripcion, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Valor unidad: \$${product.valorUnidad}", style = MaterialTheme.typography.bodySmall)
            }

            if(showAddToShopping){
                IconButton(
                    onClick = onAddToCart
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar al carrito"
                    )
                }
            }

        }
    }
}

@Composable
fun ProductPage(
    productUiState: DataUiState<List<Producto>>,
    onProductClick: (sku: String) -> Unit = {},
    onAddToCartClick: (sku: String) -> Unit = {},
    onViewDetailClick: (id: String) -> Unit = {},
    showAddToShopping: Boolean = true,
    navController: NavHostController? = null
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        DataFetchStates(
            uiState = productUiState,
            errorMessage = R.string.loading_failed_products
        ) {
            if (productUiState !is DataUiState.Success) return@DataFetchStates

            if (productUiState.data.isEmpty()) {
                EmptyItemsScreen(message = R.string.products_not_found)
                return@DataFetchStates
            }

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                productUiState.data.forEach { producto ->
                    ProductCard(
                        product = producto,
                        onClick = { onProductClick(producto.sku) },
                        onAddToCart = { onAddToCartClick(producto.sku) },
                        onViewDetail = {
                            navController?.navigate(AppPages.OrderDetailPage.createRoute(producto.sku))
                        },
                        showAddToShopping = showAddToShopping
                    )
                }
            }
        }
    }
}