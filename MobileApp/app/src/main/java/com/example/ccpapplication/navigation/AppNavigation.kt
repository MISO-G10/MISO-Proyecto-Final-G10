package com.example.ccpapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ccpapplication.AppViewModel
import com.example.ccpapplication.R
import com.example.ccpapplication.navigation.graph.AppNavGraph
import com.example.ccpapplication.navigation.graph.ClientNavigationDrawer
import com.example.ccpapplication.navigation.graph.Graph
import com.example.ccpapplication.navigation.graph.MainNavigationDrawer
import com.example.ccpapplication.services.interceptors.TokenManager

sealed class BottomNavItem(
    val route: String,
    val title: Int,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", R.string.bottom_nav_1, Icons.Filled.Home)
    object Orders : BottomNavItem("orders", R.string.bottom_nav_2, Icons.Filled.AddToPhotos)
    object Shopping : BottomNavItem("shoppingCar", R.string.bottom_nav_3, Icons.Filled.ShoppingCart)
    object Visits : BottomNavItem("visits", R.string.bottom_nav_4, Icons.Filled.AddBusiness)
    object Clients : BottomNavItem(Graph.CLIENTS, R.string.bottom_nav_5, Icons.Filled.PeopleAlt)
    object Catalog : BottomNavItem("catalog", R.string.bottom_nav_6, Icons.Filled.Inventory)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(appViewModel: AppViewModel,
                  viewModel: NavigationViewModel= viewModel(),
                  tokenManager: TokenManager
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentGraph = navBackStackEntry
        ?.destination
        ?.hierarchy
        ?.firstOrNull { it.route == Graph.ADMIN || it.route == Graph.CLIENT }
        ?.route

    Scaffold(
        topBar = {
            // Solo pintamos TopBar si estamos en cliente o admin
            if (currentGraph == Graph.CLIENT || currentGraph == Graph.ADMIN) {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = {
                            // 1) Limpiar token/usuario
                            viewModel.logout()
                            tokenManager.logOut()
                            // 2) Navegar al grafo de auth, vaciando el backstack
                            navController.navigate(Graph.AUTHENTICATION) {
                                popUpTo(Graph.AUTHENTICATION) { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = stringResource(R.string.logout)
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            when (currentGraph) {
                Graph.CLIENT  -> ClientNavigationDrawer(navController)
                Graph.ADMIN -> MainNavigationDrawer(navController)
                else        -> { /* no bottom bar en login, register, etc */ }
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