package com.example.ccpapplication.pages.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ccpapplication.R
import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.navigation.state.DataUiState
import com.example.ccpapplication.ui.components.DataFetchStates
import com.example.ccpapplication.ui.components.EmptyItemsScreen
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ClientsPage(
    navController: NavHostController = rememberNavController(),
    viewModel: ClientsViewModel = viewModel(factory = ClientsViewModel.Factory)
) {
    val clients by viewModel.clients.collectAsState()


    Scaffold(
        topBar = {
            ClientsTopBar()
        }
    ) { innerPadding ->
        ClientsList(
            clients = clients,
            navController = navController,
            contentPadding = innerPadding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text("Mis Tenderos")
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun ClientsList(
    clients: List<Client>,
    navController: NavHostController,
    contentPadding: PaddingValues
) {
    LazyColumn(
        contentPadding = contentPadding,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (clients.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay tenderos asignados")
                }
            }
        } else {
            items(clients) { client ->
                ClientItem(
                    client = client,
                    onSchedule = {
                        navController.navigate(
                            "schedule_visit/${client.id}/${client.name}/${client.telephone}/${client.address}/${client.email}"
                        )
                    },
                    onCreateOrder = {
                        navController.navigate("vendedor_shopping/${client.id}/${client.name}/${client.telephone}/${client.address}/${client.email}")
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ClientItem(
    client: Client,
    onSchedule: () -> Unit,
    onCreateOrder: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Box(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icono de tienda
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Tienda",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Información del cliente
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Nombre completo del tendero
                    Text(
                        text = "${client.name} ${client.lastName}".trim(),
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Ubicación del negocio
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ubicación",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = client.address,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Teléfono
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Teléfono",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = client.telephone,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Fecha de última visita
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Última visita",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (client.lastVisitDate != null) "Última visita: ${
                                formatDate(
                                    client.lastVisitDate
                                )
                            }" else "Sin visitas previas",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Número de visitas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Número de visitas",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Visitas: ${client.visitCount}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(start = 8.dp)
                ) {
                    Button(
                        onClick = onSchedule,
                        modifier = Modifier.testTag("schedule_button")
                    ) {
                        Text(text = stringResource(R.string.schedule_visit_label_button))
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onCreateOrder ,
                        modifier = Modifier.testTag("create_order_button")
                    ) {
                        Text("Crear pedido")
                    }
                }
            }







        }

    }
}

// Función para formatear la fecha
private fun formatDate(isoDate: String): String {
    return try {
        val dateTime = LocalDateTime.parse(isoDate, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        dateTime.format(formatter)
    } catch (e: Exception) {
        "Fecha desconocida"
    }
}
