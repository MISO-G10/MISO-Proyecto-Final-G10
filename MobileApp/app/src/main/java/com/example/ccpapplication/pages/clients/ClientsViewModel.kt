package com.example.ccpapplication.pages.clients

import com.example.ccpapplication.data.model.Client
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.ccpapplication.data.repository.ClientRepository
import com.example.ccpapplication.data.repository.ClientRepositoryImpl

class ClientsViewModel(
    private val repo: ClientRepository = ClientRepositoryImpl()  // instancia directa
) : ViewModel() {

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients

    init {
        viewModelScope.launch {
            _clients.value = repo.getClients()
        }
    }
}