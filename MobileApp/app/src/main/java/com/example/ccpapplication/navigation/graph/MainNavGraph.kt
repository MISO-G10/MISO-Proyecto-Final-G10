package com.example.ccpapplication.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.navigation.BottomDrawer
import com.example.ccpapplication.navigation.BottomNavItem
import com.example.ccpapplication.navigation.graph.Graph.SCHEDULE_VISIT
import com.example.ccpapplication.navigation.graph.Graph.VENDEDOR_SHOPPING
import com.example.ccpapplication.pages.clients.ClientsPage
import com.example.ccpapplication.pages.visits.VisitsPage
import com.example.ccpapplication.pages.clients.ScheduleVisitPage
import com.example.ccpapplication.pages.home.HomePage
import com.example.ccpapplication.pages.products.ProductPage
import com.example.ccpapplication.pages.products.ProductViewModel
import com.example.ccpapplication.pages.shopping.ShoppingCartViewModel
import com.example.ccpapplication.pages.shopping.VendedorTenderoPage
import com.example.ccpapplication.services.interceptors.TokenManager
import com.example.ccpapplication.navigation.AppPages
import com.example.ccpapplication.pages.clients.OrderViewModel
import com.example.ccpapplication.pages.orders.OrderPage
import com.example.ccpapplication.pages.orders.OrderDetailPage

fun NavGraphBuilder.mainNavGraph(navController: NavHostController,tokenManager:TokenManager) {
    // Definimos primero la ruta del detalle de orden
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

    navigation(
        route = Graph.ADMIN,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) {
            HomePage()
        }

        composable(BottomNavItem.Clients.route) {
            ClientsPage(navController = navController)
        }
        composable(BottomNavItem.Visits.route) {
            VisitsPage()
        }

        composable(BottomNavItem.Orders.route) {
            val orderViewModel: OrderViewModel = viewModel(factory = OrderViewModel.Factory)

            OrderPage(
                orderUiState = orderViewModel.orderUiState,
                navController = navController,
                viewModel = orderViewModel,
                userId = tokenManager.getUser()?.id ?: "",
                onViewDetailOrder = { orderId ->
                    navController.navigate("order/$orderId") {
                        popUpTo(BottomNavItem.Orders.route)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "$SCHEDULE_VISIT/{id}/{name}/{telephone}/{address}/{username}"
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val telephone = backStackEntry.arguments?.getString("telephone") ?: ""
            val address = backStackEntry.arguments?.getString("address") ?: ""
            val username = backStackEntry.arguments?.getString("username") ?: ""

            val client = Client(
                id = id,
                name = name,
                telephone = telephone,
                address = address,
                email = username
            )

            ScheduleVisitPage(navController = navController, client = client)
        }
        composable(BottomNavItem.Catalog.route){
            val productViewModel:ProductViewModel=
                viewModel(factory=ProductViewModel.Factory)
            val cartViewModel: ShoppingCartViewModel = viewModel(
                factory = ShoppingCartViewModel.provideFactory(tokenManager.getUser()?.id ?: "", LocalContext.current)
            )
            ProductPage(
                productUiState=productViewModel.productUiState ,
                showAddToShopping=false,
                cartViewModel = cartViewModel
            )
        }
        composable("$VENDEDOR_SHOPPING/{id}/{name}/{telephone}/{address}/{username}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val telephone = backStackEntry.arguments?.getString("telephone") ?: ""
            val address = backStackEntry.arguments?.getString("address") ?: ""
            val username = backStackEntry.arguments?.getString("username") ?: ""

            val tendero = User(
                id = id,
                telefono = telephone,
                direccion = address,
                username = username,
                password = name,
                rol = "TENDERO",
                name = ""
            )

            val tenderoId = backStackEntry.arguments?.getString("id") ?: return@composable
            val context = LocalContext.current

            val cartViewModel: ShoppingCartViewModel = viewModel(
                factory = ShoppingCartViewModel.provideFactory(
                    userId = tokenManager.getUser()?.id ?: "", // vendedor actual
                    context = context,
                    overrideUserId = tenderoId // carrito del tendero
                )
            )

            val productViewModel: ProductViewModel = viewModel(factory = ProductViewModel.Factory)

            VendedorTenderoPage(
                cartViewModel = cartViewModel,
                navController = navController,
                productViewModel = productViewModel,
                user = tokenManager.getUser(),
                tendero = tendero,
            )
        }

    }
}

@Composable
fun MainNavigationDrawer(
    navController: NavHostController,
) {

    val menus = listOf(
        BottomNavItem.Home,
        BottomNavItem.Visits,
        BottomNavItem.Clients,
        BottomNavItem.Catalog
    )
    BottomDrawer(navController,menus)
}