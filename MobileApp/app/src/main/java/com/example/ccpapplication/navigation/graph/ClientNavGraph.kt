package com.example.ccpapplication.navigation.graph

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.ccpapplication.navigation.BottomDrawer
import com.example.ccpapplication.navigation.BottomNavItem
import com.example.ccpapplication.pages.home.HomePage
import com.example.ccpapplication.pages.products.ProductPage
import com.example.ccpapplication.pages.products.ProductViewModel
import com.example.ccpapplication.pages.shopping.ShoppingCartPage
import com.example.ccpapplication.pages.shopping.ShoppingCartViewModel
import com.example.ccpapplication.services.interceptors.TokenManager

fun NavGraphBuilder.clientNavGraph(navController: NavHostController,tokenManager: TokenManager) {
    navigation(
        route = Graph.CLIENT,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) {
            HomePage()
        }
        composable(BottomNavItem.Catalog.route){
            val productViewModel:ProductViewModel=
                viewModel(factory=ProductViewModel.Factory)
            val cartViewModel: ShoppingCartViewModel = viewModel(
                factory = ShoppingCartViewModel.provideFactory(tokenManager.getUser()?.id ?: "", LocalContext.current)
            )
            ProductPage(
                productUiState=productViewModel.productUiState ,
                showAddToShopping=true,
                cartViewModel = cartViewModel
            )
        }
        composable(BottomNavItem.Shopping.route){
            val cartViewModel: ShoppingCartViewModel = viewModel(
                factory = ShoppingCartViewModel.provideFactory(tokenManager.getUser()?.id ?: "", LocalContext.current)
            )
            ShoppingCartPage(
                cartViewModel = cartViewModel,
                navController = navController,
                user = tokenManager.getUser()
            )
        }
        composable(BottomNavItem.Orders.route) {

        }



    }
}
@Composable
fun ClientNavigationDrawer(
    navController: NavController,
) {

    val scope = rememberCoroutineScope()
    val menus = listOf(
        BottomNavItem.Home,
        BottomNavItem.Catalog,
        BottomNavItem.Shopping,
        BottomNavItem.Orders

    )
    BottomDrawer(navController,menus)
}
@Preview
@Composable
fun navigationDrawerPreview(){
    Column(Modifier.fillMaxSize()) {

    }
}