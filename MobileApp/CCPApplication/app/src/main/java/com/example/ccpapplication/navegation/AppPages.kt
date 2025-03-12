package com.example.ccpapplication.navegation

sealed class AppPages (val route: String) {
    object LoginPage : AppPages(route = "login")
    object AlbumCataloguePage : AppPages(route = "albumCatalogue")
    object AlbumDetailPage : AppPages(route = "albumDetail")
    object AlbumCreatePage : AppPages(route = "albumCreate")
    object ArtistCataloguePage : AppPages(route = "artistCatalogue")
    object ArtistDetailPage : AppPages(route = "artistDetail")
    object CollectorCataloguePage : AppPages(route = "collectorCatalogue")
    object CollectorDetailPage : AppPages(route = "collectorDetail")
}