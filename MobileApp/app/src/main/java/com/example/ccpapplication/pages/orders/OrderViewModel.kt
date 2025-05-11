package com.example.ccpapplication.pages.orders


import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ccpapplication.App
import com.example.ccpapplication.data.model.Producto
import com.example.ccpapplication.data.repository.InventaryRepository
import com.example.ccpapplication.navigation.state.DataUiState
import com.example.ccpapplication.pages.products.ProductViewModel
import retrofit2.HttpException
import java.io.IOException


class OrderViewModel(
    private val inventaryRepository: InventaryRepository
) : ViewModel() {
    var productUiState: DataUiState<List<Producto>> by mutableStateOf(DataUiState.Loading)

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            productUiState = try {
                val result = inventaryRepository.getProductos()
                if (result.isSuccess) {
                    val products = result.getOrNull() ?: emptyList()  // Si es nulo, asignamos una lista vacía
                    DataUiState.Success(products)
                } else {
                    DataUiState.Error
                }
            } catch (e: IOException) {
                DataUiState.Error
            }
            catch (e: HttpException) {
                DataUiState.Error
            }
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                val inventaryRepository = application.container.inventarioRepository
                ProductViewModel(inventaryRepository = inventaryRepository)
            }
        }
    }
}