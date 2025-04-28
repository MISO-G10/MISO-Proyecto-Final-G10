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
                _clients.value = clientRepository.getClients()
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