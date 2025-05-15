package com.example.ccpapplication.pages.shopping

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*

import com.example.ccpapplication.data.model.Producto

@Composable
fun RemoveFromCartDialog(
    product: Producto,
    currentQuantity: Int,
    onDismiss: () -> Unit,
    onRemove: (cantidad: Int) -> Unit
) {
    var quantityText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quitar del carrito") },
        text = {
            Column {
                Text("Cantidad actual en el carrito: $currentQuantity")
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) quantityText = it
                        error = false
                    },
                    isError = error,
                    label = { Text("Cantidad a quitar") },
                    singleLine = true
                )
                if (error) {
                    Text("Cantidad inválida", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val cantidad = quantityText.toIntOrNull()
                if (cantidad == null || cantidad <= 0 || cantidad > currentQuantity) {
                    error = true
                } else {
                    onRemove(cantidad)
                    onDismiss()
                }
            }) {
                Text("Quitar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}