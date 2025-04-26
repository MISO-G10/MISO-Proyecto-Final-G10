package com.example.ccpapplication.pages.clients

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ccpapplication.R
import com.example.ccpapplication.data.model.Client
import com.example.ccpapplication.ui.components.ButtonType
import com.example.ccpapplication.ui.components.GenericButton
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleVisitPage(
    navController: NavHostController,
    client: Client,
    viewModel: ScheduleVisitViewModel = viewModel(factory = ScheduleVisitViewModel.Factory)
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val selectedDate = if (viewModel.date.isNotEmpty()) LocalDate.parse(viewModel.date) else LocalDate.now()
    val selectedFromTime = if (viewModel.hourFrom.isNotEmpty()) LocalTime.parse(viewModel.hourFrom) else LocalTime.of(9, 0)
    val selectedToTime = if (viewModel.hourTo.isNotEmpty()) LocalTime.parse(viewModel.hourTo) else LocalTime.of(10, 0)

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.idUser = client.id
    }

    // Prepare calendar for initial date/year/month/day
    val calendar = Calendar.getInstance().apply {
        set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val selectedDate = LocalDate.of(year, month + 1, day)
            if (selectedDate.isBefore(LocalDate.now())) {
                Toast.makeText(context, "La fecha no puede ser anterior a hoy", Toast.LENGTH_SHORT).show()
                // No update to date
            } else {
                viewModel.date = selectedDate.toString()
            }
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val fromTimeDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val selectedTime = LocalTime.of(hour, minute)
            if (!selectedTime.isBefore(selectedToTime)) {
                Toast.makeText(context, "La hora de inicio debe ser antes de la hora de fin", Toast.LENGTH_SHORT).show()
                // No update
            } else {
                viewModel.hourFrom = selectedTime.format(timeFormatter)
            }
        },
        selectedFromTime.hour,
        selectedFromTime.minute,
        true
    )

    val toTimeDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val selectedTime = LocalTime.of(hour, minute)
            if (!selectedTime.isAfter(selectedFromTime)) {
                Toast.makeText(context, "La hora de fin debe ser después de la hora de inicio", Toast.LENGTH_SHORT).show()
                // No update
            } else {
                viewModel.hourTo = selectedTime.format(timeFormatter)
            }
        },
        selectedToTime.hour,
        selectedToTime.minute,
        true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card de resumen de cliente
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3E7FF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(client.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(client.telephone, style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Fecha
        val dateInteractionSource = remember { MutableInteractionSource() }
        OutlinedTextField(
            value = selectedDate.format(dateFormatter),
            onValueChange = {},
            label = { Text("Fecha") },
            modifier = Modifier
                .fillMaxWidth(),
            readOnly = true,
            interactionSource = dateInteractionSource
        )
        androidx.compose.runtime.LaunchedEffect(dateInteractionSource) {
            dateInteractionSource.interactions.collect { interaction ->
                if (interaction is androidx.compose.foundation.interaction.PressInteraction.Release) {
                    datePickerDialog.show()
                }
            }
        }

        // Desde / Hasta
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val fromTimeInteractionSource = remember { MutableInteractionSource() }
            OutlinedTextField(
                value = selectedFromTime.format(timeFormatter),
                onValueChange = {},
                label = { Text("Desde") },
                modifier = Modifier
                    .weight(1f),
                readOnly = true,
                interactionSource = fromTimeInteractionSource
            )
            androidx.compose.runtime.LaunchedEffect(fromTimeInteractionSource) {
                fromTimeInteractionSource.interactions.collect { interaction ->
                    if (interaction is androidx.compose.foundation.interaction.PressInteraction.Release) {
                        fromTimeDialog.show()
                    }
                }
            }

            val toTimeInteractionSource = remember { MutableInteractionSource() }
            OutlinedTextField(
                value = selectedToTime.format(timeFormatter),
                onValueChange = {},
                label = { Text("Hasta") },
                modifier = Modifier
                    .weight(1f),
                readOnly = true,
                interactionSource = toTimeInteractionSource
            )
            androidx.compose.runtime.LaunchedEffect(toTimeInteractionSource) {
                toTimeInteractionSource.interactions.collect { interaction ->
                    if (interaction is androidx.compose.foundation.interaction.PressInteraction.Release) {
                        toTimeDialog.show()
                    }
                }
            }
        }

        // Notas
        OutlinedTextField(
            value = viewModel.comments,
            onValueChange = { viewModel.comments = it },
            label = { Text("Notas") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.weight(1f))

//        // Botón Agendar
//        Button(
//            onClick = { /* lógica para guardar la visita */ },
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//            Text("Agendar")
//        }


        GenericButton(
            label = stringResource(R.string.register_register_button_label),
            onClick = {
                viewModel.addVisit { success ->
                    // La navegación se maneja en el LaunchedEffect
                }
            },
            type = ButtonType.PRIMARY,
            modifier = Modifier.fillMaxWidth()
        )
    }
}