package com.example.ccpapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ccpapplication.navigation.graph.AppNavGraph
import com.example.ccpapplication.navigation.graph.NavigationDrawer

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Inicio", Icons.Filled.Home)
    object Orders : BottomNavItem("orders", "Ordenes", Icons.Filled.AddToPhotos)
    object Shopping : BottomNavItem("shoppingCar", "Carrito", Icons.Filled.ShoppingCart)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = when (currentRoute) {
        AppPages.HomePage.route, BottomNavItem.Home.route, BottomNavItem.Orders.route, BottomNavItem.Shopping.route -> true
        else -> false
    }
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationDrawer(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}