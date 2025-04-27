package com.example.ccpapplication.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.ccpapplication.AppViewModel

object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val CLIENT = "client_graph"
    const val ADMIN="admin_graph"
    const val SCHEDULE_VISIT = "schedule_visit"
    const val CLIENTS = "clients"
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController(),
                appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        route = "root",
        startDestination = Graph.AUTHENTICATION
    ) {
        authNavGraph(navController = navController,appViewModel = appViewModel)
        clientNavGraph(navController = navController)
        mainNavGraph(navController = navController)
    }
}