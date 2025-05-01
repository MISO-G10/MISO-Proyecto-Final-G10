package com.example.ccpapplication.pages.clients

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.data.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClientsPageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Repositorio falso para pruebas que devuelve una lista predefinida de clientes
    private class FakeClientRepository(private val clientsToReturn: List<Client>) : ClientRepository {
        override suspend fun getClients(): List<Client> {
            return clientsToReturn
        }
    }

    // Implementación de prueba del ViewModel que usa un repositorio falso
    private class TestClientsViewModel(clients: List<Client>) : ClientsViewModel(FakeClientRepository(clients)) {
        // Inicializar los clientes directamente en el constructor
        init {
            // Forzar la carga de clientes inmediatamente
            loadClients()
        }
        
        // Sobrescribir el método handleError para evitar usar Log
        override fun handleError(e: Exception) {
            // No hacer nada en las pruebas
        }
    }

    private fun createTestViewModel(clients: List<Client>): ClientsViewModel {
        return TestClientsViewModel(clients)
    }

    @Test
    fun clientsPage_displaysTopBar() {
        // Configurar
        composeTestRule.setContent {
            ClientsTopBar()
        }

        // Verificar
        composeTestRule.onNodeWithText("Mis Tenderos").assertIsDisplayed()
    }

    @Test
    fun clientsPage_withNoClients_showsEmptyMessage() {
        // Configurar
        val emptyList = emptyList<Client>()
        val testViewModel = createTestViewModel(emptyList)

        composeTestRule.setContent {
            ClientsPage(
                navController = rememberNavController(),
                viewModel = testViewModel
            )
        }

        // Verificar
        composeTestRule.onNodeWithText("No hay tenderos asignados").assertIsDisplayed()
    }


    @Test
    fun clientsPage_withMultipleClients_displaysAllClients() {
        // Configurar
        val testClients = listOf(
            Client(
                id = "1",
                name = "Juan",
                lastName = "Pérez",
                email = "juan@example.com",
                telephone = "123456789",
                address = "Calle 123",
                lastVisitDate = "2025-01-01T10:00:00Z",
                visitCount = 5
            ),
            Client(
                id = "2",
                name = "María",
                lastName = "González",
                email = "maria@example.com",
                telephone = "987654321",
                address = "Avenida 456",
                lastVisitDate = "2025-01-02T15:30:00Z",
                visitCount = 3
            )
        )
        val testViewModel = createTestViewModel(testClients)

        composeTestRule.setContent {
            ClientsPage(
                navController = rememberNavController(),
                viewModel = testViewModel
            )
        }

        // Verificar que se muestran ambos clientes
        composeTestRule.onNodeWithText("Juan Pérez").assertIsDisplayed()
        composeTestRule.onNodeWithText("María González").assertIsDisplayed()
    }


    @Test
    fun clientItem_withNoLastVisitDate_showsNoVisitsMessage() {
        // Configurar
        val client = Client(
            id = "1",
            name = "Juan",
            lastName = "Pérez",
            email = "juan@example.com",
            telephone = "123456789",
            address = "Calle 123",
            lastVisitDate = null,
            visitCount = 0
        )

        composeTestRule.setContent {
            ClientItem(
                client = client,
                onSchedule = {}
            )
        }

        // Verificar que se muestra el mensaje de sin visitas previas
        composeTestRule.onNodeWithText("Sin visitas previas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visitas: 0").assertIsDisplayed()
    }

    @Test
    fun clientItem_scheduleButton_isClickable() {
        // Configurar
        val client = Client(
            id = "1",
            name = "Juan",
            lastName = "Pérez",
            email = "juan@example.com",
            telephone = "123456789",
            address = "Calle 123",
            lastVisitDate = "2025-01-01T10:00:00Z",
            visitCount = 5
        )
        
        var buttonClicked = false
        
        composeTestRule.setContent {
            ClientItem(
                client = client,
                onSchedule = { buttonClicked = true }
            )
        }

        // Hacer clic en el botón Agendar
        composeTestRule.onNodeWithText("Agendar").performClick()
        
        // Verificar que se llamó a la función onSchedule
        assert(buttonClicked)
    }
}
