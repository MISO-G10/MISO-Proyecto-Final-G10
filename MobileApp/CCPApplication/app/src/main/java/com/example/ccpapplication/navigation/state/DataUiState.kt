package com.example.ccpapplication.navigation.state

sealed interface DataUiState<in T> {
    data class Success<T>(val data: T) : DataUiState<T>
    object Error : DataUiState<Any>
    object Loading : DataUiState<Any>
}