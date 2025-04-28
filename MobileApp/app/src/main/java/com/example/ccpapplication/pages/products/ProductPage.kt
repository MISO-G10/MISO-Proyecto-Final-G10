package com.example.ccpapplication.pages.products
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.navigation.state.DataUiState
import com.example.ccpapplication.ui.components.DataFetchStates
import com.example.ccpapplication.ui.components.EmptyItemsScreen
import com.example.ccpapplication.R
import kotlin.Boolean

@Composable
fun ProductCard(
    product: Producto,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,  // Añadir un nuevo parámetro para el botón de agregar al carrito
    showAddToShopping: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
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
    onAddToCartClick: (sku: String) -> Unit = {}, // Agregar una función para manejar el evento del carrito
    showAddToShopping: Boolean=true
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
                        onAddToCart = { onAddToCartClick(producto.sku) }, // Llamar a la función de agregar al carrito
                        showAddToShopping
                    )
                }
            }
        }
    }
}

