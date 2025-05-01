package com.example.ccpapplication.pages.clients

import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.data.repository.ClientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClientsViewModelTest {

    private lateinit var viewModel: TestClientsViewModel
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeClientRepository: FakeClientRepository

    // Repositorio falso para pruebas
    private class FakeClientRepository : ClientRepository {
        var shouldThrowError = false
        var clientsToReturn = listOf<Client>()

        override suspend fun getClients(): List<Client> {
            if (shouldThrowError) {
                throw Exception("Error al obtener clientes")
            }
            return clientsToReturn
        }
    }
    
    // Subclase de ClientsViewModel para pruebas que sobrescribe el método handleError
    private class TestClientsViewModel(
        clientRepository: ClientRepository
    ) : ClientsViewModel(clientRepository) {
        var lastError: Exception? = null
        
        override fun handleError(e: Exception) {
            // Simplemente guardamos el error para verificarlo en las pruebas
            // sin usar android.util.Log
            lastError = e
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeClientRepository = FakeClientRepository()
        viewModel = TestClientsViewModel(fakeClientRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadClients_success_updatesClientsState() = runTest {
        // Preparar datos de prueba
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
        
        fakeClientRepository.clientsToReturn = testClients
        
        // Ejecutar la función a probar
        viewModel.loadClients()
        
        // Avanzar el tiempo para que se complete la corrutina
        testScheduler.advanceUntilIdle()
        
        // Verificar que el estado se actualizó correctamente
        val clients = viewModel.clients.first()
        assertEquals(2, clients.size)
        assertEquals("2", clients[0].id) // Debería estar ordenado por fecha más reciente
        assertEquals("1", clients[1].id)
    }

    @Test
    fun loadClients_withNullDates_sortsCorrectly() = runTest {
        // Preparar datos de prueba con fechas nulas o vacías
        val testClients = listOf(
            Client(
                id = "1",
                name = "Juan",
                lastName = "Pérez",
                email = "juan@example.com",
                telephone = "123456789",
                address = "Calle 123",
                lastVisitDate = null,
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
            ),
            Client(
                id = "3",
                name = "Carlos",
                lastName = "Rodríguez",
                email = "carlos@example.com",
                telephone = "555555555",
                address = "Plaza 789",
                lastVisitDate = "",
                visitCount = 0
            )
        )
        
        fakeClientRepository.clientsToReturn = testClients
        
        // Ejecutar la función a probar
        viewModel.loadClients()
        
        // Avanzar el tiempo para que se complete la corrutina
        testScheduler.advanceUntilIdle()
        
        // Verificar que el estado se actualizó correctamente y está ordenado
        val clients = viewModel.clients.first()
        assertEquals(3, clients.size)
        assertEquals("2", clients[0].id) // Cliente con fecha más reciente
        // Los clientes sin fecha deberían estar al final, pero no importa su orden entre ellos
        assertTrue(clients[1].id == "1" || clients[1].id == "3")
        assertTrue(clients[2].id == "1" || clients[2].id == "3")
    }

    @Test
    fun loadClients_withInvalidDates_handlesErrorGracefully() = runTest {
        // Preparar datos de prueba con fechas inválidas
        val testClients = listOf(
            Client(
                id = "1",
                name = "Juan",
                lastName = "Pérez",
                email = "juan@example.com",
                telephone = "123456789",
                address = "Calle 123",
                lastVisitDate = "fecha-invalida",
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
        
        fakeClientRepository.clientsToReturn = testClients
        
        // Ejecutar la función a probar
        viewModel.loadClients()
        
        // Avanzar el tiempo para que se complete la corrutina
        testScheduler.advanceUntilIdle()
        
        // Verificar que el estado se actualizó correctamente a pesar de la fecha inválida
        val clients = viewModel.clients.first()
        assertEquals(2, clients.size)
        assertEquals("2", clients[0].id) // Cliente con fecha válida debería estar primero
        assertEquals("1", clients[1].id) // Cliente con fecha inválida debería estar al final
    }

    @Test
    fun loadClients_error_keepsEmptyList() = runTest {
        // Configurar el repositorio para que lance un error
        fakeClientRepository.shouldThrowError = true
        
        // Ejecutar la función a probar
        viewModel.loadClients()
        
        // Avanzar el tiempo para que se complete la corrutina
        testScheduler.advanceUntilIdle()
        
        // Verificar que el estado sigue siendo una lista vacía
        val clients = viewModel.clients.first()
        assertTrue(clients.isEmpty())
        
        // Verificar que se capturó el error
        assertNotNull(viewModel.lastError)
        assertEquals("Error al obtener clientes", viewModel.lastError?.message)
    }

    @Test
    fun init_callsLoadClients() = runTest {
        // Preparar datos de prueba
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
            )
        )
        
        fakeClientRepository.clientsToReturn = testClients
        
        // Crear una nueva instancia del ViewModel (esto debería llamar a loadClients en init)
        val newViewModel = TestClientsViewModel(fakeClientRepository)
        
        // Avanzar el tiempo para que se complete la corrutina
        testScheduler.advanceUntilIdle()
        
        // Verificar que el estado se actualizó correctamente
        val clients = newViewModel.clients.first()
        assertEquals(1, clients.size)
        assertEquals("1", clients[0].id)
    }
}
