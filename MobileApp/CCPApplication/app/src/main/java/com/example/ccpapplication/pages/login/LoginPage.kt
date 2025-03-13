package com.example.ccpapplication.pages.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccpapplication.ui.components.ButtonType

import com.example.ccpapplication.ui.components.CircleWithText
import com.example.ccpapplication.ui.components.GenericButton

@Composable
fun Login(
    userViewModel: LoginViewModel
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        CircleWithText(circleSize = 100.dp, text="CCP")
        FormLayout(
            formState = userViewModel.formState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            onTogglePasswordVisibility = { userViewModel.togglePasswordVisibility() },
        )
    }
}
@Composable
fun FormLayout(
    modifier: Modifier = Modifier,
    formState: LoginPageState,
    onTogglePasswordVisibility: () -> Unit
){

    val (email,password,passwordVisible) = formState

    Column(

        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = email.value,
            placeholder = { Text("JhonDoe@gmail.com") },
            onValueChange = { newText -> email.value = newText },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Correo Electrónico") },
            isError = false,

            )
        OutlinedTextField(
            value = password.value,
            placeholder = { Text("**********") },
            onValueChange = { newText -> password.value = newText },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            visualTransformation =  if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            isError = false,
            trailingIcon = {
                val image = if (passwordVisible.value)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                val description = if (passwordVisible.value) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { onTogglePasswordVisibility() }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )
        GenericButton(
            label = "Iniciar sesión",
            onClick = { /*TODO*/ },
            type = ButtonType.PRIMARY,
            modifier = Modifier.fillMaxWidth()
        )

        Row {
            Text(
                text = "No tienes cuenta aun? ",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.scrim,

                    fontSize = 14.sp
                )
            )

            Text(
                text = "Registrate",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                    ),

            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {

    Login(
        userViewModel = viewModel(factory = LoginViewModel.Factory)
    )
}