package com.example.ccpapplication.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.ccpapplication.navigation.BottomDrawer
import com.example.ccpapplication.navigation.BottomNavItem
import com.example.ccpapplication.pages.clients.ClientsPage
import com.example.ccpapplication.pages.clients.ScheduleVisitPage
import com.example.ccpapplication.pages.home.HomePage

import com.example.ccpapplication.navigation.graph.Graph.SCHEDULE_VISIT

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
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

        composable(SCHEDULE_VISIT) {
            ScheduleVisitPage(navController = navController)
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