package com.example.ccpapplication.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ccpapplication.ui.theme.AppTheme


enum class ButtonType {
    PRIMARY, SECONDARY, TERTIARY, ALTERNATIVE,

}

@Composable
fun GenericButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    type: ButtonType = ButtonType.PRIMARY,
    icon: ImageVector? = null,
) {
    val hasIcon = icon != null

    val shape = if (hasIcon) {
        CircleShape
    } else ButtonDefaults.shape

    val customModifier = if (type == ButtonType.ALTERNATIVE) {
        modifier.shadow(elevation = 6.dp, shape = shape)
    } else {
        modifier
    }

    val padding = if (hasIcon) {
        PaddingValues(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
    } else ButtonDefaults.ContentPadding

    if (type == ButtonType.SECONDARY) {
        OutlinedButton(
            onClick = onClick,
            modifier = customModifier,
            shape = shape,
            contentPadding = padding
        ) {
            ButtonContent(icon, label, type)
        }
        return
    }

    Button(
        contentPadding = padding,
        onClick = onClick, colors = ButtonDefaults.buttonColors(
            containerColor = when (type) {
                ButtonType.PRIMARY -> MaterialTheme.colorScheme.primary
                ButtonType.ALTERNATIVE -> MaterialTheme.colorScheme.primaryContainer
                else -> Color.Transparent
            }, contentColor = when (type) {
                ButtonType.PRIMARY -> White
                ButtonType.ALTERNATIVE -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.primary
            }
        ),
        shape = shape,
        modifier = customModifier
    ) {
        Box {
            ButtonContent(icon, label, type)
        }
    }
}
@Composable
fun ButtonContent(icon: ImageVector?, label: String?, type: ButtonType) {
    if (icon != null) {
        val color = when (type) {
            ButtonType.PRIMARY -> White
            ButtonType.ALTERNATIVE -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }

        Icon(
            imageVector = icon,
            contentDescription = icon.name,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    } else {
        label?.let { Text(text = it) }
    }
}
@Preview(showBackground = true, widthDp = 300)
@Composable
fun GenericButtonPreview() {
    AppTheme{
        Column {
            Row{
                GenericButton(
                    label = "Iniciar sesión",
                    onClick = { /*TODO*/ },
                    type = ButtonType.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row {



                Spacer(Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GenericButton(
                        label = "Save",
                        onClick = { /*TODO*/ },
                        type = ButtonType.SECONDARY,
                        modifier = Modifier.width(100.dp)
                    )
                    GenericButton(
                        label = "Save",
                        onClick = { /*TODO*/ },
                        type = ButtonType.TERTIARY,
                        modifier = Modifier.width(100.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GenericButton(
                        icon = Icons.Outlined.Add,
                        onClick = { /*TODO*/ },
                        type = ButtonType.PRIMARY,
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                    )
                    Spacer(Modifier.height(5.dp))
                    GenericButton(
                        icon = Icons.Outlined.Add,
                        onClick = { /*TODO*/ },
                        type = ButtonType.SECONDARY,
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                    )
                    Spacer(Modifier.height(5.dp))
                    GenericButton(
                        icon = Icons.Outlined.Create,
                        onClick = { /*TODO*/ },
                        type = ButtonType.ALTERNATIVE,
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                    )
                    Spacer(Modifier.height(5.dp))


                }
            }
        }

    }

}