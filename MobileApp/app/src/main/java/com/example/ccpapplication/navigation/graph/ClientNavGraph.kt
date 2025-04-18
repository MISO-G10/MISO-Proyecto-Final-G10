package com.example.ccpapplication.navigation.graph

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.example.ccpapplication.navigation.BottomDrawer
import com.example.ccpapplication.navigation.BottomNavItem
import com.example.ccpapplication.pages.home.HomePage
import com.example.ccpapplication.pages.orders.Order

fun NavGraphBuilder.clientNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.CLIENT,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) {
            HomePage()
        }
        composable(BottomNavItem.Orders.route) {
            Order()
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
        BottomNavItem.Orders,
        BottomNavItem.Shopping
    )
    BottomDrawer(navController,menus)
}
@Preview
@Composable
fun navigationDrawerPreview(){
    Column(Modifier.fillMaxSize()) {

    }
}