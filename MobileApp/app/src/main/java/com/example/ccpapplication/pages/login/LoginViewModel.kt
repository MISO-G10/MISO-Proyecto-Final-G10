package com.example.ccpapplication.pages.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.ccpapplication.App
import com.example.ccpapplication.R
import com.example.ccpapplication.data.model.UserLogin
import com.example.ccpapplication.data.repository.UserRepository
import com.example.ccpapplication.navigation.AppPages
import com.example.ccpapplication.navigation.graph.Graph
import com.example.ccpapplication.navigation.state.DataUiState
import com.example.ccpapplication.services.interceptors.TokenManager
import com.example.ccpapplication.util.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

data class LoginPageState(
    val email: MutableState<String>,
    val password: MutableState<String>,
    val passwordVisible:MutableState<Boolean>,
    val selectedLanguage: MutableState<String>
)


class LoginViewModel(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
): ViewModel() {

    private val _messageEvent = MutableSharedFlow<UiText>()
    val messageEvent = _messageEvent

    var uiState: DataUiState<UserLogin> by mutableStateOf(DataUiState.Loading)
    var formState: LoginPageState by mutableStateOf(
        LoginPageState(
            email = mutableStateOf(""),
            password = mutableStateOf(""),
            passwordVisible= mutableStateOf(false),
            selectedLanguage=mutableStateOf("en")
        )
    )



    fun loginUser(navController: NavController) {
        viewModelScope.launch {
            uiState = DataUiState.Loading
            val user = UserLogin(
                username = formState.email.value,
                password = formState.password.value,
            )

            val result = userRepository.login(user)

            result.fold(
                onSuccess = { authResponse ->
                    val user = tokenManager.getUser()

                    when {
                        user == null -> {
                            viewModelScope.launch {
                                _messageEvent.emit(UiText.StringResource(R.string.user_not_found))
                            }
                            uiState = DataUiState.Error
                        }

                        user.rol in listOf("ADMINISTRADOR", "VENDEDOR", "DIRECTOR_VENTAS") -> {
                            navController.navigate(Graph.ADMIN) {
                                popUpTo(Graph.AUTHENTICATION) { inclusive = true }
                            }

                            viewModelScope.launch {
                                _messageEvent.emit(UiText.StringResource(R.string.welcome_message, user.username))
                            }

                            uiState = DataUiState.Success(authResponse)
                        }

                        user.rol == "TENDERO" -> {
                            navController.navigate(Graph.CLIENT) {
                                popUpTo(Graph.AUTHENTICATION) { inclusive = true }
                            }

                            viewModelScope.launch {
                                _messageEvent.emit(UiText.StringResource(R.string.welcome_message, user.username))
                            }

                            uiState = DataUiState.Success(authResponse)
                        }

                        else -> {

                            viewModelScope.launch {
                                _messageEvent.emit(UiText.StringResource(R.string.role_not_found, user.rol))
                            }

                            uiState = DataUiState.Error
                        }
                    }
                },
                onFailure = { exception ->

                    _messageEvent.emit(
                        UiText.StringResource(R.string.generic_error, exception.localizedMessage ?: R.string.unknown_error)
                    )
                }
            )
        }
    }
    fun togglePasswordVisibility() {
        formState.passwordVisible.value = !formState.passwordVisible.value
    }
    fun onToggleLanguage(language:String){
        formState.selectedLanguage.value=language
    }
    fun navigateRegisterPage(navController: NavController){
        navController.navigate(AppPages.RegisterPage.route)

    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                val userRepository = application.container.userRepository
                val tokenManager = application.container.tokenManager
                LoginViewModel(userRepository = userRepository, tokenManager = tokenManager)
            }
        }
    }
}