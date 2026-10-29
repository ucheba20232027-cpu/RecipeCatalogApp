package com.example.recipecataloglab.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonStyle { PRIMARY, SECONDARY, OUTLINED }
enum class ButtonSize { SMALL, MEDIUM, LARGE }

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    size: ButtonSize = ButtonSize.MEDIUM,
    enabled: Boolean = true
) {
    val padding = when (size) {
        ButtonSize.SMALL -> PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ButtonSize.MEDIUM -> PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ButtonSize.LARGE -> PaddingValues(horizontal = 32.dp, vertical = 14.dp)
    }
    val font = when (size) {
        ButtonSize.SMALL -> 12.sp
        ButtonSize.MEDIUM -> 15.sp
        ButtonSize.LARGE -> 18.sp
    }

    val content: @Composable () -> Unit = {
        Text(text, fontSize = font)
    }

    when (style) {
        ButtonStyle.PRIMARY -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            contentPadding = padding
        ) { content() }

        ButtonStyle.SECONDARY -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            contentPadding = padding,
            colors = ButtonDefaults.filledTonalButtonColors()
        ) { content() }

        ButtonStyle.OUTLINED -> OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            contentPadding = padding
        ) { content() }
    }
}