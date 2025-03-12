package com.example.ccpapplication.pages.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.ccpapplication.ui.components.CircleWithText

@Composable
fun Login(
    userViewModel: LoginViewModel
) {
    Column(
        verticalArrangement = Arrangement.Top,
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
                .padding(16.dp)
        )
    }
}
@Composable
fun FormLayout(
    modifier: Modifier = Modifier,
    formState: LoginPageState
){

    val (email,password) = formState

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
            placeholder = { Text("******") },
            onValueChange = { newText -> password.value = newText },
            singleLine = false,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            isError = false,
        )
    }
}
@Preview(showBackground = true)
@Composable
fun HomePagePreview() {

    Login(
        userViewModel = viewModel(factory = LoginViewModel.Factory)
    )
}