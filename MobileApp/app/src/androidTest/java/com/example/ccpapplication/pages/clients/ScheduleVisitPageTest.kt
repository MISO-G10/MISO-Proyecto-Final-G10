package com.example.ccpapplication.pages.clients

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ccpapplication.data.model.Client
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScheduleVisitPageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeClient = Client(
        id = "123",
        name = "Tienda Test",
        telephone = "1234567890",
        address = "Calle Test 123",
        email = "test@test.com"
    )

    private fun assertTextExists(vararg texts: String) {
        val found = texts.any { text ->
            composeTestRule.onAllNodesWithText(text, substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        assert(found) { "Ninguno de los textos encontrados: ${texts.joinToString()}" }
    }

    @Test
    fun screen_displaysClientInformationCorrectly() {
        composeTestRule.setContent {
            ScheduleVisitPage(
                navController = androidx.navigation.compose.rememberNavController(),
                client = fakeClient
            )
        }

        composeTestRule.onNodeWithText("Tienda Test").assertIsDisplayed()
        composeTestRule.onNodeWithText("1234567890").assertIsDisplayed()

        assertTextExists("Date", "Fecha")
        assertTextExists("From", "Desde")
        assertTextExists("To", "Hasta")
        assertTextExists("Notes", "Notas")
        assertTextExists("Schedule visit", "Agendar visita")
    }
}