package com.example.ccpapplication.pages.shopping

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.pages.products.ProductPage
import com.example.ccpapplication.pages.products.ProductViewModel

@Composable
fun VendedorTenderoPage(
    navController: NavController,
    cartViewModel: ShoppingCartViewModel,
    productViewModel: ProductViewModel,
    user: User?,
    tendero: User?
) {

    LazyColumn (
        modifier = Modifier
            .fillMaxSize()

    ){
    item{
        ShoppingCartPage(
            cartViewModel = cartViewModel,
            navController = navController, // puedes también pasarlo como argumento
            user = user, // si no necesitas mostrar datos del tendero
            tendero = tendero,
            enableLazyColumnScroll = false
        )
        HorizontalDivider(thickness = 3.dp)
    }
    item {
        ProductPage(
            productUiState = productViewModel.productUiState,
            showAddToShopping = true,
            cartViewModel = cartViewModel,
            enableLazyColumnScroll = false

        )

        HorizontalDivider(thickness = 1.dp)
    }






    }
}