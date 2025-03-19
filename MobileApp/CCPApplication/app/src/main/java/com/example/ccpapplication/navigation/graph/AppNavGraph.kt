package com.example.ccpapplication.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val MAIN = "main_graph"
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController(),
modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        route = "root",
        startDestination = Graph.AUTHENTICATION
    ) {
        authNavGraph(navController = navController)
        mainNavGraph(navController = navController)
    }
}