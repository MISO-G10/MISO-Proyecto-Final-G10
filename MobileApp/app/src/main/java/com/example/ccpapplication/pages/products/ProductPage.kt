package com.example.ccpapplication.pages.products
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.navigation.state.DataUiState
import com.example.ccpapplication.ui.components.DataFetchStates
import com.example.ccpapplication.ui.components.EmptyItemsScreen
import com.example.ccpapplication.R
import com.example.ccpapplication.pages.shopping.ShoppingCartViewModel
import kotlin.Boolean

@Composable
fun ProductCard(
    product: Producto,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onViewDetail:()->Unit,
    showAddToShopping: Boolean
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
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
                    text = product.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )


                Text(
                    text = product.descripcion,
                    style = MaterialTheme.typography.bodyMedium
                )


                Text(
                    text = "Valor unidad: \$${product.valorUnidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = "Disponibles: ${product.cantidadTotal} Unidades",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.width(15.dp))

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

                if (showAddToShopping) {
                    IconButton(onClick = onAddToCart) {
                        Icon(
                            imageVector = Icons.Filled.AddShoppingCart,
                            contentDescription = "Agregar al carrito",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun ProductPage(
    productUiState: DataUiState<List<Producto>>,
    cartViewModel: ShoppingCartViewModel,
    onProductClick: (id: String) -> Unit = {},
    onViewDetailProduct: (id: String) -> Unit = {},
    showAddToShopping: Boolean = true,
    enableLazyColumnScroll: Boolean = true // <-- nuevo parámetro
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar producto") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        DataFetchStates(
            uiState = productUiState,
            errorMessage = R.string.loading_failed_products
        ) {
            if (productUiState !is DataUiState.Success) return@DataFetchStates

            val filteredProducts = productUiState.data.filter {
                it.nombre.contains(searchQuery, ignoreCase = true)
            }

            if (filteredProducts.isEmpty()) {
                EmptyItemsScreen(message = R.string.products_not_found)
                return@DataFetchStates
            }

            if (enableLazyColumnScroll) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts) { producto ->
                        ProductCardWithDialog(
                            product = producto,
                            onClick = { onProductClick(producto.id) },
                            cartViewModel = cartViewModel,
                            onViewDetail = { onViewDetailProduct(producto.id) },
                            showAddToShopping = showAddToShopping
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteredProducts.forEach { producto ->
                        ProductCardWithDialog(
                            product = producto,
                            onClick = { onProductClick(producto.id) },
                            cartViewModel = cartViewModel,
                            onViewDetail = { onViewDetailProduct(producto.id) },
                            showAddToShopping = showAddToShopping
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ProductCardWithDialog(
    product: Producto,
    cartViewModel: ShoppingCartViewModel,
    onClick: () -> Unit,
    onViewDetail: () -> Unit,
    showAddToShopping: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }
    val puedeAgregar = cartViewModel.couldAddProduct(product)

    ProductCard(
        product = product,
        onClick = onClick,
        onAddToCart = { showDialog = true },
        onViewDetail = onViewDetail,
        showAddToShopping = showAddToShopping && puedeAgregar
    )

    if (showDialog) {
        AddToCartDialog(
            product = product,
            maxQuantity = product.cantidadTotal,
            onDismiss = { showDialog = false },
            onAdd = { cantidad ->
                cartViewModel.addToCart(product, cantidad)
            }
        )
    }
}


