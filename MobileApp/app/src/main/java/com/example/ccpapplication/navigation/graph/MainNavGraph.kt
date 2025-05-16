package com.example.ccpapplication.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.navigation.BottomDrawer
import com.example.ccpapplication.navigation.BottomNavItem
import com.example.ccpapplication.navigation.graph.Graph.SCHEDULE_VISIT
import com.example.ccpapplication.pages.clients.ClientsPage
import com.example.ccpapplication.pages.visits.VisitsPage
import com.example.ccpapplication.pages.clients.ScheduleVisitPage
import com.example.ccpapplication.pages.home.HomePage
import com.example.ccpapplication.pages.products.ProductPage
import com.example.ccpapplication.pages.products.ProductViewModel
import com.example.ccpapplication.pages.shopping.ShoppingCartViewModel
import com.example.ccpapplication.services.interceptors.TokenManager

fun NavGraphBuilder.mainNavGraph(navController: NavHostController,tokenManager:TokenManager) {
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