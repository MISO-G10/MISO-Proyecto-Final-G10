package com.example.ccpapplication.pages.login

import android.widget.Toast
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
import com.example.ccpapplication.App
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.model.UserLogin
import com.example.ccpapplication.data.repository.UserRepository
import com.example.ccpapplication.navegation.state.DataUiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class LoginPageState(
    val email: MutableState<String>,
    val password: MutableState<String>,
)

class LoginViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    var uiState: DataUiState<UserLogin> by mutableStateOf(DataUiState.Loading)
    var formState: LoginPageState by mutableStateOf(
        LoginPageState(
            email = mutableStateOf(""),
            password = mutableStateOf("")
        )
    )
    fun loginUser(){
        viewModelScope.launch {
            uiState = try {
                val user = UserLogin(
                    username = formState.email.value,
                    password = formState.password.value,

                    )
                val createResult = userRepository.login(user)

                DataUiState.Success(
                    createResult
                )
            }catch (e: IOException) {

                DataUiState.Error
            } catch (e: HttpException) {

                DataUiState.Error
            }

        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                val userRepository = application.container.userRepository
                LoginViewModel(userRepository = userRepository)
            }
        }
    }
}