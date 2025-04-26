package com.example.ccpapplication.pages.register

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ccpapplication.R
import com.example.ccpapplication.ui.components.ButtonType
import com.example.ccpapplication.ui.components.GenericButton
import com.example.ccpapplication.ui.components.LoadingDialog

@Composable
fun RegisterPage(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val confirmCreation = stringResource(R.string.register_success_message)

    //Evento de registro exitoso
    LaunchedEffect(viewModel.registrationSuccessful) {
        if (viewModel.registrationSuccessful) {
            //Mostrar mensaje de éxito
            Toast.makeText(context, confirmCreation, Toast.LENGTH_SHORT).show()

            // Navegar a la página de inicio de sesión o principal después del registro exitoso
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Formulario de registro
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = stringResource(R.string.register_page_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center
            )

            // Campo Nombre
            OutlinedTextField(
                value = viewModel.firstName,
                onValueChange = { viewModel.firstName = it },
                label = { Text(stringResource(R.string.register_firstname_input_label)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = viewModel.firstNameError != null,
                supportingText = {
                    viewModel.firstNameError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Apellido
            OutlinedTextField(
                value = viewModel.lastName,
                onValueChange = { viewModel.lastName = it },
                label = { Text(stringResource(R.string.register_lastname_input_label)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = viewModel.lastNameError != null,
                supportingText = {
                    viewModel.lastNameError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Email
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text(stringResource(R.string.register_email_input_label)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = viewModel.emailError != null,
                supportingText = {
                    viewModel.emailError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Contraseña
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text(stringResource(R.string.register_password_input_label)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description = if (passwordVisible) stringResource(R.string.login_password_input_trailing_true) else stringResource(R.string.login_password_input_trailing_false)

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = viewModel.passwordError != null,
                supportingText = {
                    viewModel.passwordError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    } ?: Text(stringResource(R.string.register_validation_error_pwd_length), fontSize = 12.sp)
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Confirmar Contraseña
            OutlinedTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
                label = { Text(stringResource(R.string.register_confirmpwd_input_label)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description = if (confirmPasswordVisible) stringResource(R.string.login_password_input_trailing_true) else stringResource(R.string.login_password_input_trailing_false)

                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = viewModel.confirmPasswordError != null,
                supportingText = {
                    viewModel.confirmPasswordError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Tipo de Cliente (deshabilitado con valor por defecto TENDERO)
            OutlinedTextField(
                value = viewModel.clientType,
                onValueChange = {},
                label = { Text(stringResource(R.string.register_client_type_input_label)) },
                readOnly = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje de error general
            viewModel.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botón de registro
            GenericButton(
                label = stringResource(R.string.register_register_button_label),
                onClick = {
                    viewModel.register { success ->
                        // La navegación se maneja en el LaunchedEffect
                    }
                },
                type = ButtonType.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace para ir a inicio de sesión
            Row {
                Text(
                    text = stringResource(R.string.register_login_sub_1),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.scrim,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.register_login_button_label),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.clickable {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }
        }

        // Mensaje para proceso de creación de cuenta
        if (viewModel.isLoading) {
            LoadingDialog(message = stringResource(R.string.register_saving_message))
        }
    }
}