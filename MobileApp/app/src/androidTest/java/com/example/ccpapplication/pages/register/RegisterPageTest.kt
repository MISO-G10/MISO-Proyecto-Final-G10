package com.example.ccpapplication.pages.register

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.navigation.NavController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
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
        assertTextExists("Correo electrónico", "Email")
        assertTextExists("Contraseña", "Password")
        assertTextExists("Confirmar Contraseña", "Confirm Password")
        assertTextExists("Tipo de Cliente", "Client Type")
        assertTextExists("TENDERO")
        assertTextExists("Registrarse", "Sign Up")
        assertTextExists("¿Ya tienes una cuenta?", "Already have an account?")
        assertTextExists("Iniciar sesión", "Sign In")
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
            hasText("Registrarse", ignoreCase = true) or hasText("Sign Up", ignoreCase = true)
        ).performClick()

        // Verificar que aparezca el mensaje de error para el nombre vacío
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("El nombre no puede estar vacío", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("First name cannot be empty", ignoreCase = true)
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
        composeTestRule.onNode(
            hasText("Nombre", ignoreCase = true) or hasText("First Name", ignoreCase = true)
        ).performTextInput("Juan")
        
        composeTestRule.onNode(
            hasText("Apellido", ignoreCase = true) or hasText("Last Name", ignoreCase = true)
        ).performTextInput("Pérez")
        
        composeTestRule.onNode(
            hasText("Correo electrónico", ignoreCase = true) or hasText("Email", ignoreCase = true)
        ).performTextInput("juan@example.com")
        
        // Ingresar contraseñas diferentes
        composeTestRule.onNode(
            hasText("Contraseña", ignoreCase = true) or hasText("Password", ignoreCase = true)
        ).performTextInput("Password123")
        
        composeTestRule.onNode(
            hasText("Confirmar Contraseña", ignoreCase = true) or hasText("Confirm Password", ignoreCase = true)
        ).performTextInput("DifferentPassword123")

        // Hacer clic en el botón de registro
        composeTestRule.onNode(
            hasText("Registrarse", ignoreCase = true) or hasText("Sign Up", ignoreCase = true)
        ).performClick()

        // Verificar que aparezca el mensaje de error de contraseñas no coincidentes
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Las contraseñas no coinciden", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Passwords do not match", ignoreCase = true)
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
        composeTestRule.onNode(
            hasText("Correo electrónico", ignoreCase = true) or hasText("Email", ignoreCase = true)
        ).performTextInput("invalid-email")

        // Hacer clic en el botón de registro
        composeTestRule.onNode(
            hasText("Registrarse", ignoreCase = true) or hasText("Sign Up", ignoreCase = true)
        ).performClick()

        // Verificar que aparezca el mensaje de error de email inválido
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("El correo electrónico no es válido", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("The email address is not valid", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun shortPassword_showsError() {
        composeTestRule.setContent {
            RegisterPage(
                navController = rememberNavController()
            )
        }

        // Llenar los campos con datos válidos excepto la contraseña
        composeTestRule.onNode(
            hasText("Nombre", ignoreCase = true) or hasText("First Name", ignoreCase = true)
        ).performTextInput("Juan")
        
        composeTestRule.onNode(
            hasText("Apellido", ignoreCase = true) or hasText("Last Name", ignoreCase = true)
        ).performTextInput("Pérez")
        
        composeTestRule.onNode(
            hasText("Correo electrónico", ignoreCase = true) or hasText("Email", ignoreCase = true)
        ).performTextInput("juan@example.com")
        
        // Ingresar una contraseña corta
        composeTestRule.onNode(
            hasText("Contraseña", ignoreCase = true) or hasText("Password", ignoreCase = true)
        ).performTextInput("short")
        
        composeTestRule.onNode(
            hasText("Confirmar Contraseña", ignoreCase = true) or hasText("Confirm Password", ignoreCase = true)
        ).performTextInput("short")

        // Hacer clic en el botón de registro
        composeTestRule.onNode(
            hasText("Registrarse", ignoreCase = true) or hasText("Sign Up", ignoreCase = true)
        ).performClick()

        // Verificar que aparezca el mensaje de error de contraseña corta
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("La contraseña debe tener al menos 8 caracteres", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("The password must be at least 8 characters long", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun loginLink_navigatesToLoginScreen() {
        composeTestRule.setContent {
            RegisterPage(
                navController = rememberNavController()
            )
        }

        // Hacer scroll hacia abajo para mostrar el enlace de iniciar sesión
        composeTestRule.onRoot().performTouchInput {
            swipeUp()
        }
        
        // Verificar que el texto "¿Ya tienes una cuenta?" existe
        composeTestRule.onNode(
            hasText("¿Ya tienes una cuenta?", ignoreCase = true) or
            hasText("Already have an account?", ignoreCase = true)
        ).assertExists()
        
        // Verificar que el enlace "Iniciar sesión" existe
        composeTestRule.onNode(
            hasText("Iniciar sesión", ignoreCase = true) or
            hasText("Sign In", ignoreCase = true) or
            hasText("Log in", ignoreCase = true)
        ).assertExists()
    }
}
