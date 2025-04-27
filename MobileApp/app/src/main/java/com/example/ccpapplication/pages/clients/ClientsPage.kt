package com.example.ccpapplication.pages.clients


import com.example.ccpapplication.data.model.Client
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ccpapplication.R

@Composable
fun ClientsPage(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: ClientsViewModel = viewModel()
) {
    val clients by viewModel.clients.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Clientes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(top = 20.dp, bottom = 8.dp)
        ) {
            items(clients) { client ->
                ClientItem(
                    client = client,
                    onSchedule = {
                        navController.navigate(
                            "schedule_visit/${client.id}/${client.name}/${client.telephone}/${client.address}/${client.email}"
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ClientItem(
    client: Client,
    modifier: Modifier = Modifier,
    onSchedule: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Reemplazamos el Row por un Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Contenido (avatar + textos) alineado al inicio superior
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = client.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Contacto: ${client.email}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = client.address,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Botón en la esquina superior derecha, solo "Agendar"
            Button(
                onClick = onSchedule,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(text = stringResource(R.string.schedule_visit_button_label))
            }
        }
    }
}
