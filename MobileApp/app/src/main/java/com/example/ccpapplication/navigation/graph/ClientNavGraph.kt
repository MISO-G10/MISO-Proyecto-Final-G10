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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.ccpapplication.navigation.BottomDrawer
import com.example.ccpapplication.navigation.BottomNavItem
import com.example.ccpapplication.pages.clients.OrderViewModel
import com.example.ccpapplication.pages.home.HomePage
import com.example.ccpapplication.pages.orders.OrderDetailPage
import com.example.ccpapplication.pages.orders.OrderPage
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
                user = tokenManager.getUser(),
                tendero = null
            )
        }
        composable(BottomNavItem.Orders.route) {
            val orderViewModel: OrderViewModel = viewModel(factory = OrderViewModel.Factory)

            OrderPage(
                orderUiState = orderViewModel.orderUiState,
                navController = navController,
                viewModel = orderViewModel,
                userId = tokenManager.getUser()?.id ?: "",
                onViewDetailOrder = { orderId ->
                    navController.navigate("order/$orderId")
                }
            )
        }

        composable(
            route = "order/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val orderViewModel: OrderViewModel = viewModel(factory = OrderViewModel.Factory)
            OrderDetailPage(
                orderId = orderId,
                viewModel = orderViewModel,
                navController = navController
            )
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