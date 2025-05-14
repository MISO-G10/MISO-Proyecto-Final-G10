package com.example.ccpapplication.pages.products

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class ProductListPageTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    @Test
    fun screen_displaysProductListCorrectly(){
        composeTestRule.setContent {
            val fakeRepository = FakeInventaryRepository()
            val testViewModel = ProductViewModel(fakeRepository)

            ProductPage(productUiState=testViewModel.productUiState ,
                showAddToShopping=true)

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