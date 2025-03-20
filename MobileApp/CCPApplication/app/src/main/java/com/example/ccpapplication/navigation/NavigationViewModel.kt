package com.example.ccpapplication.navigation

import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import com.example.ccpapplication.navigation.state.NavigationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class NavigationViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(NavigationUiState(logged = false))
    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

}