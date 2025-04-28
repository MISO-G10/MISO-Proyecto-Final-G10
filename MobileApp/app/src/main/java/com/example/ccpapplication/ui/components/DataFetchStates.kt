package com.example.ccpapplication.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ccpapplication.navigation.state.DataUiState
import com.example.ccpapplication.R
@Composable
fun DataFetchStates(uiState: DataUiState<*>, @StringRes errorMessage: Int, renderOnSuccess: @Composable () -> Unit) {

    when (uiState) {
        is DataUiState.Loading -> LoadingScreen(
            R.string.loading,
            modifier = Modifier.fillMaxSize()
        )

        is DataUiState.Success -> {
            renderOnSuccess()
        }

        is DataUiState.Error -> ErrorScreen(
            errorMessage,
            modifier = Modifier.fillMaxSize()
        )
    }
}