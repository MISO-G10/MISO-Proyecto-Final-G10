package com.example.ccpapplication.pages.visits

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ccpapplication.App
import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.data.model.Visit
import com.example.ccpapplication.data.repository.ClientRepository
import com.example.ccpapplication.data.repository.VisitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

enum class VisitFilter(val label: String) {
    ALL("Todas"),
    COMPLETED("Realizadas"),
    IN_PROGRESS("En Proceso"),
    CANCELED("Canceladas")
}

open class VisitsViewModel(
    private val clientRepository: ClientRepository,
    private val visitRepository: VisitRepository
) : ViewModel() {

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients

    private val _currentFilter = MutableStateFlow(VisitFilter.ALL)
    val currentFilter: StateFlow<VisitFilter> = _currentFilter

    val filteredClients: StateFlow<List<Client>> = combine(_clients, _currentFilter) { clients, filter ->
        clients.map { client ->
            val filteredVisits = when (filter) {
                VisitFilter.ALL -> client.visits
                VisitFilter.COMPLETED -> client.visits.filter { visit ->
                    val visitDateTime = LocalDateTime.parse("${visit.date}T${visit.hourTo}")
                    visitDateTime.isBefore(LocalDateTime.now()) && !visit.canceled
                }
                VisitFilter.IN_PROGRESS -> client.visits.filter { visit ->
                    val visitDate = LocalDateTime.parse("${visit.date}T${visit.hourFrom}").toLocalDate()
                    val visitStart = LocalDateTime.parse("${visit.date}T${visit.hourFrom}")
                    val visitEnd = LocalDateTime.parse("${visit.date}T${visit.hourTo}")
                    val now = LocalDateTime.now()
                    visitDate == now.toLocalDate() && now.isAfter(visitStart) && now.isBefore(visitEnd)
                }
                VisitFilter.CANCELED -> client.visits.filter { it.canceled }
            }

            client.copy(visits = filteredVisits)
        }.filter { it.visits.isNotEmpty() || filter == VisitFilter.ALL }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadClients()
    }

    fun setFilter(filter: VisitFilter) {
        _currentFilter.value = filter
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

    fun cancelVisit(visit: Visit) {
        viewModelScope.launch {
            try {
                // Crear una copia de la visita con el estado "cancelada" actualizado
                val updatedVisit = visit.copy(canceled = true)

                // Llamar al repositorio para actualizar la visita
                val result = visitRepository.updateVisit(updatedVisit)

                if (result.isSuccess) {
                    // Si la actualización fue exitosa, recargar los clientes
                    loadClients()
                } else {
                    handleError(Exception("Error al cancelar la visita: ${result.exceptionOrNull()?.message}"))
                }
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
                val visitRepository = application.container.visitRepository
                VisitsViewModel(clientRepository, visitRepository)
            }
        }
    }
}