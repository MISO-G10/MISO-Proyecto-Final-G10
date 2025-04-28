package com.example.ccpapplication.pages.clients

import android.util.Log
import com.example.ccpapplication.data.model.Client
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ccpapplication.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.ccpapplication.data.repository.ClientRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ClientsViewModel(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients

    init {
        loadClients()
    }
    
    fun loadClients() {
        viewModelScope.launch {
            try {
                // Obtener los clientes del repositorio
                val clientsList = clientRepository.getClients()
                
                val sortedClients = clientsList.sortedByDescending { client ->
                    try {
                        if (client.lastVisitDate != null && client.lastVisitDate.isNotEmpty()) {
                            // Intentar parsear la fecha
                            LocalDateTime.parse(client.lastVisitDate, DateTimeFormatter.ISO_DATE_TIME)
                        } else {
                            // Si no hay fecha, colocar al final
                            LocalDateTime.MIN
                        }
                    } catch (e: DateTimeParseException) {
                        // Si hay error al parsear, colocar al final
                        LocalDateTime.MIN
                    }
                }
                
                _clients.value = sortedClients
            } catch (e: Exception) {
                Log.e("ClientsViewModel", "Error al cargar tenderos: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                val clientRepository = application.container.clientRepository
                ClientsViewModel(clientRepository)
            }
        }
    }
}