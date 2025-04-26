package com.example.ccpapplication.navigation.graph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.navigation.BottomDrawer
import com.example.ccpapplication.navigation.BottomNavItem
import com.example.ccpapplication.pages.clients.ClientsPage
import com.example.ccpapplication.pages.clients.ScheduleVisitPage
import com.example.ccpapplication.pages.home.HomePage

import com.example.ccpapplication.navigation.graph.Graph.SCHEDULE_VISIT

@RequiresApi(Build.VERSION_CODES.O)
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