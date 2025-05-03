package com.example.ccpapplication.pages.visits

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

open class VisitsViewModel(
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
                handleError(e)
            }
        }
    }

    // Método protegido para manejar errores, que puede ser sobrescrito en pruebas
    protected open fun handleError(e: Exception) {
        Log.e("VisitsViewModel", "Error al cargar tenderos y sus visitas: ${e.message}")
        e.printStackTrace()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                val clientRepository = application.container.clientRepository
                VisitsViewModel(clientRepository)
            }
        }
    }
}