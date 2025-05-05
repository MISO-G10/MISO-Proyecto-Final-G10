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
import androidx.compose.foundation.interaction.PressInteraction
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
import com.example.ccpapplication.navigation.graph.Graph
import com.example.ccpapplication.ui.components.ButtonType
import com.example.ccpapplication.ui.components.GenericButton
import com.example.ccpapplication.ui.components.LoadingDialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import android.view.ContextThemeWrapper

private val DefaultDate: LocalDate = LocalDate.now()
private val DefaultHourFrom: LocalTime = LocalTime.of(8, 0)
private val DefaultHourTo: LocalTime = LocalTime.of(14, 0)
private val TimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun ScheduleVisitPage(
    navController: NavHostController,
    client: Client,
    viewModel: ScheduleVisitViewModel = viewModel(factory = ScheduleVisitViewModel.Factory)
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val selectedDate = if (viewModel.date.isNotEmpty()) LocalDate.parse(viewModel.date) else DefaultDate
    val selectedFromTime = if (viewModel.hourFrom.isNotEmpty()) LocalTime.parse(viewModel.hourFrom) else DefaultHourFrom
    val selectedToTime = if (viewModel.hourTo.isNotEmpty()) LocalTime.parse(viewModel.hourTo) else DefaultHourTo
    val confirmCreation = stringResource(R.string.add_visit_success_message)

    val context = LocalContext.current

    val errorDateText = stringResource(R.string.add_visit_validation_error_date)
    val errorHourFromText = stringResource(R.string.add_visit_validation_error_hour_from)
    val errorHourToText = stringResource(R.string.add_visit_validation_error_hour_to)

    LaunchedEffect(Unit) {
        viewModel.idUser = client.id
        if (viewModel.date.isBlank()) {
            viewModel.date = DefaultDate.toString()
        }
        if (viewModel.hourFrom.isBlank()) {
            viewModel.hourFrom = DefaultHourFrom.format(TimeFormatter)
        }
        if (viewModel.hourTo.isBlank()) {
            viewModel.hourTo = DefaultHourTo.format(TimeFormatter)
        }
    }

    //Evento de para agregar visita
    LaunchedEffect(viewModel.addVisitSuccessful) {
        if (viewModel.addVisitSuccessful) {
            //Mostrar mensaje de éxito
            Toast.makeText(context, confirmCreation, Toast.LENGTH_SHORT).show()

            // Navegar a la página de Clientes
            navController.navigate(Graph.CLIENTS) {
                popUpTo(Graph.SCHEDULE_VISIT) { inclusive = true }
            }
        } else if (viewModel.errorMessage != null) {
            // Mostrar mensaje de error
            Toast.makeText(context, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // Prepare calendar for initial date/year/month/day
    val calendar = Calendar.getInstance().apply {
        set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
    }

    val datePickerDialog = DatePickerDialog(
        ContextThemeWrapper(context, R.style.CustomPickerDialog),
        { _, year, month, day ->
            val selectedDate = LocalDate.of(year, month + 1, day)
            if (selectedDate.isBefore(LocalDate.now())) {
                Toast.makeText(context, errorDateText, Toast.LENGTH_SHORT).show()
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
        ContextThemeWrapper(context, R.style.CustomPickerDialog),
        { _, hour, minute ->
            val selectedTime = LocalTime.of(hour, minute)
            if (!selectedTime.isBefore(selectedToTime)) {
                Toast.makeText(context, errorHourFromText, Toast.LENGTH_SHORT).show()
                // No update
            } else {
                viewModel.hourFrom = selectedTime.format(TimeFormatter)
            }
        },
        selectedFromTime.hour,
        selectedFromTime.minute,
        true
    )

    val toTimeDialog = TimePickerDialog(
        ContextThemeWrapper(context, R.style.CustomPickerDialog),
        { _, hour, minute ->
            val selectedTime = LocalTime.of(hour, minute)
            if (!selectedTime.isAfter(selectedFromTime)) {
                Toast.makeText(context, errorHourToText, Toast.LENGTH_SHORT).show()
                // No update
            } else {
                viewModel.hourTo = selectedTime.format(TimeFormatter)
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
                Text(client.telephone ?: "Sin teléfono", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Fecha
        val dateInteractionSource = remember { MutableInteractionSource() }
        OutlinedTextField(
            value = selectedDate.format(dateFormatter),
            onValueChange = {},
            label = { Text(stringResource(R.string.add_visit_label_date)) },
            modifier = Modifier
                .fillMaxWidth(),
            readOnly = true,
            interactionSource = dateInteractionSource
        )
        LaunchedEffect(dateInteractionSource) {
            dateInteractionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) {
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
                value = selectedFromTime.format(TimeFormatter),
                onValueChange = {},
                label = { Text(stringResource(R.string.add_visit_label_hour_from)) },
                modifier = Modifier
                    .weight(1f),
                readOnly = true,
                interactionSource = fromTimeInteractionSource
            )
            LaunchedEffect(fromTimeInteractionSource) {
                fromTimeInteractionSource.interactions.collect { interaction ->
                    if (interaction is PressInteraction.Release) {
                        fromTimeDialog.show()
                    }
                }
            }

            val toTimeInteractionSource = remember { MutableInteractionSource() }
            OutlinedTextField(
                value = selectedToTime.format(TimeFormatter),
                onValueChange = {},
                label = { Text(stringResource(R.string.add_visit_label_hour_to)) },
                modifier = Modifier
                    .weight(1f),
                readOnly = true,
                interactionSource = toTimeInteractionSource
            )
            LaunchedEffect(toTimeInteractionSource) {
                toTimeInteractionSource.interactions.collect { interaction ->
                    if (interaction is PressInteraction.Release) {
                        toTimeDialog.show()
                    }
                }
            }
        }

        // Notas
        OutlinedTextField(
            value = viewModel.comments,
            onValueChange = {
                viewModel.comments = it
                if (viewModel.commentsError != null && it.isNotBlank()) {
                    viewModel.commentsError = null
                }
            },
            label = { Text(stringResource(R.string.add_visit_label_comments)) },
            isError = viewModel.commentsError != null,
            supportingText = {
                viewModel.commentsError?.let {
                    Text(text = it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.weight(1f))

        GenericButton(
            label = stringResource(R.string.schedule_visit_label_button),
            onClick = {
                viewModel.addVisit { _ ->
                    // La navegación se maneja en el LaunchedEffect
                }
            },
            type = ButtonType.PRIMARY,
            modifier = Modifier.fillMaxWidth()
        )

        if (viewModel.isLoading) {
            LoadingDialog(message = stringResource(R.string.register_saving_message))
        }
    }
}
