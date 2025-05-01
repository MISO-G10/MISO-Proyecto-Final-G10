package com.example.ccpapplication.pages.register

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterPageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun assertTextExists(vararg texts: String) {
        val found = texts.any { text ->
            composeTestRule.onAllNodesWithText(text, substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        assert(found) { "Ninguno de los textos encontrados: ${texts.joinToString()}" }
    }

    @Test
    fun screen_displaysAllFormFieldsCorrectly() {
        composeTestRule.setContent {
            RegisterPage(
                navController = rememberNavController()
            )
        }

        // Verificar que todos los campos del formulario estén presentes
        assertTextExists("Nueva Cuenta", "New Account")
        assertTextExists("Nombre", "First Name")
        assertTextExists("Apellido", "Last Name")
        assertTextExists("Email", "Correo")
        assertTextExists("Contraseña", "Password")
        assertTextExists("Confirmar", "Confirm")
        assertTextExists("TENDERO")
        assertTextExists("Registrarse", "Register")
        assertTextExists("¿Ya tienes una cuenta?", "Already have an account?")
        assertTextExists("Iniciar sesión", "Login")
    }

    @Test
    fun emptyFirstName_showsError() {
        composeTestRule.setContent {
            RegisterPage(
                navController = rememberNavController()
            )
        }

        // Hacer clic en el botón de registro sin llenar el campo de nombre
        composeTestRule.onNode(
            hasText("Registrarse", ignoreCase = true) or hasText("Register", ignoreCase = true)
        ).performClick()

        // Verificar que aparezca el mensaje de error para el nombre vacío
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("El nombre no puede estar vacío", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("First name cannot be empty", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun passwordMismatch_showsError() {
        composeTestRule.setContent {
            RegisterPage(
                navController = rememberNavController()
            )
        }

        // Llenar los campos con datos válidos excepto las contraseñas
        composeTestRule.onNodeWithText("Nombre", substring = true, ignoreCase = true)
            .performTextInput("Juan")
        composeTestRule.onNodeWithText("Apellido", substring = true, ignoreCase = true)
            .performTextInput("Pérez")
        composeTestRule.onNodeWithText("Email", substring = true, ignoreCase = true)
            .performTextInput("juan@example.com")
        
        // Ingresar contraseñas diferentes
        composeTestRule.onAllNodesWithText("Contraseña", substring = true, ignoreCase = true)
            .fetchSemanticsNodes().firstOrNull()?.let {
                composeTestRule.onNode(hasText("Contraseña", substring = true, ignoreCase = true))
                    .performTextInput("Password123")
            }
        
        composeTestRule.onNodeWithText("Confirmar", substring = true, ignoreCase = true)
            .performTextInput("DifferentPassword123")

        // Hacer clic en el botón de registro
        composeTestRule.onNode(
            hasText("Registrarse", ignoreCase = true) or hasText("Register", ignoreCase = true)
        ).performClick()

        // Verificar que aparezca el mensaje de error de contraseñas no coincidentes
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Las contraseñas no coinciden", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Passwords do not match", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun invalidEmail_showsError() {
        composeTestRule.setContent {
            RegisterPage(
                navController = rememberNavController()
            )
        }

        // Llenar el campo de email con un valor inválido
        composeTestRule.onNodeWithText("Email", substring = true, ignoreCase = true)
            .performTextInput("invalid-email")

        // Hacer clic en el botón de registro
        composeTestRule.onNode(
            hasText("Registrarse", ignoreCase = true) or hasText("Register", ignoreCase = true)
        ).performClick()

        // Verificar que aparezca el mensaje de error de email inválido
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("El email no es válido", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Invalid email format", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun loginLink_navigatesToLoginScreen() {
        val navigated = mutableListOf<String>()
        
        composeTestRule.setContent {
            val navController = rememberNavController()
            RegisterPage(navController = navController)
            
            // Capturar la navegación
            navController.addOnDestinationChangedListener { _, destination, _ ->
                navigated.add(destination.route ?: "")
            }
        }

        // Hacer clic en el enlace de inicio de sesión
        composeTestRule.onNode(
            hasText("Iniciar sesión", ignoreCase = true) or hasText("Login", ignoreCase = true)
        ).performClick()

        // Verificar que se navegue a la pantalla de login
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            navigated.contains("login")
        }
    }
}
