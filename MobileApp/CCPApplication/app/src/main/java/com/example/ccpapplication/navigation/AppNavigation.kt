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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ccpapplication.AppViewModel
import com.example.ccpapplication.R
import com.example.ccpapplication.navigation.graph.AppNavGraph
import com.example.ccpapplication.navigation.graph.NavigationDrawer

sealed class BottomNavItem(
    val route: String,
    val title: Int,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", R.string.bottom_nav_1, Icons.Filled.Home)
    object Orders : BottomNavItem("orders", R.string.bottom_nav_2, Icons.Filled.AddToPhotos)
    object Shopping : BottomNavItem("shoppingCar", R.string.bottom_nav_3, Icons.Filled.ShoppingCart)
}

@Composable
fun AppNavigation(appViewModel: AppViewModel,
                  viewModel: NavigationViewModel= viewModel()
) {
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
            modifier = Modifier.padding(innerPadding),
            appViewModel = appViewModel,
        )
    }
}