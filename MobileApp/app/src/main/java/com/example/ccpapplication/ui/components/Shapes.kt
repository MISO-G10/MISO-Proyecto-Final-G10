package com.example.ccpapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ccpapplication.ui.theme.AppTheme
import com.example.ccpapplication.ui.theme.primaryContainerLight
import com.example.ccpapplication.ui.theme.primaryLight

@Composable
fun CircleWithText(circleSize: Dp= 100.dp,colorText: Color= primaryLight,color: Color=primaryContainerLight,text:String) {

    Box(
        contentAlignment = Alignment.Center, // Centra el contenido dentro del Box
        modifier = Modifier
            .size(circleSize) // Tamaño del círculo
            .background(color = color, shape = CircleShape) // Fondo circular
    ) {
        Text(
            text = text,
            color = colorText, // Color del texto
            textAlign = TextAlign.Center // Alineación del texto
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CircleWithTextPreview() {
    AppTheme {
        CircleWithText(text="CCP")
    }

}