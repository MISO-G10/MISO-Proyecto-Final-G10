package com.example.ccpapplication.navigation.graph

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.ccpapplication.AppViewModel
import com.example.ccpapplication.ChangeLanguage
import com.example.ccpapplication.navigation.AppPages
import com.example.ccpapplication.pages.login.Login
import com.example.ccpapplication.pages.login.LoginViewModel
import com.example.ccpapplication.pages.login.RegisterPage

fun NavGraphBuilder.authNavGraph(navController: NavHostController,appViewModel: AppViewModel) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AppPages.LoginPage.route
    ) {
        composable(route = AppPages.LoginPage.route) {
            val userViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
            Login(
                userViewModel = userViewModel,
                navController = navController,
                appViewModel = appViewModel
            )
        }
        composable(route = AppPages.RegisterPage.route) {
            RegisterPage()
        }
    }
}