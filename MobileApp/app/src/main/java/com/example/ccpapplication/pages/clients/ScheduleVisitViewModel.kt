package com.example.ccpapplication.pages.clients

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ccpapplication.App
import com.example.ccpapplication.R
import com.example.ccpapplication.data.model.VisitAdd
import com.example.ccpapplication.data.repository.VisitRepository
import kotlinx.coroutines.launch

class ScheduleVisitViewModel(private val errorMessages: ValidationErrorMessages,
                        private val visitRepository: VisitRepository
) : ViewModel() {
    var date by mutableStateOf("")
    var hourFrom by mutableStateOf("")
    var hourTo by mutableStateOf("")
    var comments by mutableStateOf("")
    var idUser by mutableStateOf("")

    var commentsError by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
    var addVisitSuccessful by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)


    // Función para validar los campos de entrada del formulario
    private fun validateInputs(): Boolean {
        var isValid = true

        // Validar comentarios
        if (comments.isBlank()) {
            commentsError = errorMessages.commentsEmpty
            isValid = false
        } else {
            commentsError = null
        }
        return isValid
    }

    fun addVisit(onComplete: (Boolean) -> Unit) {
        if (validateInputs()) {
            viewModelScope.launch {
                isLoading = true

                val visit = VisitAdd(
                    date = date,
                    hourFrom = hourFrom,
                    hourTo = hourTo,
                    comments = comments,
                    idUser = idUser
                )

                val result = visitRepository.addVisit(visit)

                result.fold(
                    onSuccess = { response ->
                        isLoading = false
                        addVisitSuccessful = true
                        onComplete(true)
                    },
                    onFailure = { exception ->
                        isLoading = false
                        errorMessage = exception.message
                        onComplete(false)
                    }
                )
            }
        } else {
            onComplete(false)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                val visitRepository = application.container.visitRepository
                val errorMessages = ValidationErrorMessages(
                    commentsEmpty = application.getString(R.string.add_visit_validation_error_comments_empty)
                )
                ScheduleVisitViewModel(errorMessages = errorMessages, visitRepository = visitRepository)
            }
        }
    }
}

// Clase para encapsular todos los mensajes de error de validación
data class ValidationErrorMessages(
    val commentsEmpty: String
)