package com.example.ccpapplication.pages.products

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ccpapplication.pages.shopping.InMemorySharedPreferences
import com.example.ccpapplication.pages.shopping.ShoppingCartViewModel
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class ProductListPageTest {
    private lateinit var sharedPrefs: InMemorySharedPreferences

    @get:Rule
    val composeTestRule = createComposeRule()
    @Test
    fun screen_displaysProductListCorrectly(){
        composeTestRule.setContent {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val fakeRepository = FakeInventaryRepository()
            val testViewModel = ProductViewModel(fakeRepository)
            sharedPrefs = InMemorySharedPreferences()
            val cartViewModel = ShoppingCartViewModel(
                userId = "0c1da9e2-cd13-4cdc-ad62-00dabea7f472",
                inventaryRepository = fakeRepository,
                sharedPrefs = sharedPrefs
            )
            ProductPage(
                productUiState = testViewModel.productUiState,
                showAddToShopping = true,
                onProductClick = {},
                cartViewModel = cartViewModel,
                onViewDetailProduct = {}
            )

        }
        composeTestRule.onNodeWithText("Producto 1").assertExists()
        composeTestRule.onNodeWithText("Producto 2").assertExists()


        composeTestRule.onNodeWithText("Producto de prueba 1").assertExists()
        composeTestRule.onNodeWithText("Producto de prueba 2").assertExists()


        composeTestRule.onNodeWithText("Valor unidad: \$12.5").assertExists()
        composeTestRule.onNodeWithText("Valor unidad: \$20.0").assertExists()


        composeTestRule.onAllNodesWithContentDescription("Agregar al carrito")
            .assertCountEquals(2)
    }
}