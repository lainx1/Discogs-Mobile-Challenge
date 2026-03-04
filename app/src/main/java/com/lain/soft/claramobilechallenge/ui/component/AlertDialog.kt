package com.lain.soft.claramobilechallenge.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    text: String,
    confirmationText: String,
    icon: ImageVector,
) {
    AlertDialog(
        iconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onDismissRequest,) {
                Text(confirmationText)
            }
        },
        text = {
            Text(text, style = MaterialTheme.typography.bodySmall)
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}
