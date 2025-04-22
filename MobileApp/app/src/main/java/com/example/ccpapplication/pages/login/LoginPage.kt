package com.example.ccpapplication.pages.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ccpapplication.AppViewModel
import com.example.ccpapplication.R
import com.example.ccpapplication.ui.components.ButtonType

import com.example.ccpapplication.ui.components.GenericButton

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.flow.collectLatest

@Composable
fun Login(
    userViewModel: LoginViewModel,
    navController: NavController,
    appViewModel: AppViewModel
) {

    val context = LocalContext.current

    // Escucha los eventos emitidos por el ViewModel
    LaunchedEffect(Unit) {
        userViewModel.messageEvent.collectLatest { uiText ->
            val message = uiText.asString(context)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    userViewModel.onToggleLanguage(appViewModel.getSavedLocale())
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_ccp_logo),
                contentDescription = stringResource(R.string.app_icon_tittle),
                modifier = Modifier.size(220.dp)
            )


            FormLayout(
                formState = userViewModel.formState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                onTogglePasswordVisibility = { userViewModel.togglePasswordVisibility() },
                onLoginFunction = { userViewModel.loginUser(navController) },
                onNavigateRegister = { userViewModel.navigateRegisterPage(navController) }
            )

        }
        Spacer(modifier = Modifier.height(16.dp)) // Add a space between the form and the row

        LanguageSegmentedControl(
            formState = userViewModel.formState,
            onLanguageSelected = { language ->
                appViewModel.changeLanguage(language)
                userViewModel.onToggleLanguage(language)
            }
        )
    }
}
@Composable
fun FormLayout(
    modifier: Modifier = Modifier,
    formState: LoginPageState,
    onTogglePasswordVisibility: () -> Unit,
    onLoginFunction:()-> Unit ,
    onNavigateRegister:()->Unit
){

    val (email,password,passwordVisible) = formState

    Column(

        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = email.value,
            placeholder = { Text(stringResource(R.string.login_email_input_placeholder)) },
            onValueChange = { newText -> email.value = newText },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.login_email_input_label)) },
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
            label = { Text(stringResource(R.string.login_password_input_label)) },
            isError = false,
            trailingIcon = {
                val image = if (passwordVisible.value)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                val description = if (passwordVisible.value) stringResource(R.string.login_password_input_trailing_false) else stringResource(R.string.login_password_input_trailing_true)

                IconButton(onClick = { onTogglePasswordVisibility() }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )
        GenericButton(
            label = stringResource(R.string.login_submit_button_label),
            onClick = onLoginFunction,
            type = ButtonType.PRIMARY,
            modifier = Modifier.fillMaxWidth()
        )

        Row {
            Text(
                text = stringResource(R.string.login_register_sub_1),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.scrim,

                    fontSize = 14.sp
                )
            )

            Text(
                text = stringResource(R.string.login_register_sub_2),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                    ),
                modifier = Modifier.clickable { onNavigateRegister() },

            )
        }
    }
}

@Composable
fun LanguageSegmentedControl(
    formState: LoginPageState,
    onLanguageSelected: (String) -> Unit
) {
    val cornerRadius = 8.dp
    val color = MaterialTheme.colorScheme.primary
    val paddingHorizontal = 8.dp
    val paddingVertical = 2.dp
    val borderWidth = 0.5.dp
    val height = 26.dp
    val width=200.dp

    Row(
        modifier = Modifier
            .widthIn(max = width)
            .heightIn(max = height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(color.copy(alpha = 0.1f))
            .border(width = borderWidth, color = Color.Transparent, shape = RoundedCornerShape(cornerRadius))
    ) {
        // Spanish Option
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius))
                .background(if (formState.selectedLanguage.value=="es") color else Color.Transparent)
                .clickable {
                    if (formState.selectedLanguage.value=="en") {
                        onLanguageSelected("es")
                    }
                }
                .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.login_text_lang_1),
                color = if (formState.selectedLanguage.value=="es") Color.White else color,
                fontSize = 11.sp
            )
        }

        // English Option
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius))
                .background(if (formState.selectedLanguage.value=="en") color else Color.Transparent)
                .clickable {
                    if (formState.selectedLanguage.value=="es") {
                        onLanguageSelected("en")
                    }
                }
                .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.login_text_lang_2),
                color = if (formState.selectedLanguage.value=="en") Color.White else color,
                fontSize = 11.sp
            )
        }
    }
}