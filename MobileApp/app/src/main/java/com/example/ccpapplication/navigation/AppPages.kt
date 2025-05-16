package com.example.ccpapplication.navigation

sealed class AppPages (val route: String) {
    object LoginPage : AppPages(route = "login")
    object RegisterPage : AppPages(route = "register")
    object HomePage : AppPages(route = "home")
    object ShoppingCartPage : AppPages(route = "shoppingCart")
    object clientsPage : AppPages(route = "clients")
    object OrderDetailPage : AppPages(route = "order/{orderId}") {
        fun createRoute(orderId: String) = "order/$orderId"
    }
}