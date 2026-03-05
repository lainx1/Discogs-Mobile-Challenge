package com.lain.soft.claramobilechallenge.ui.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.lain.soft.claramobilechallenge.R

@Composable
fun FilterInputDialog(
    title: String,
    initialText: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var text by rememberSaveable(initialText) { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(text = title) },
                keyboardOptions = keyboardOptions
            )
        },
        confirmButton = {
            Button(
                onClick = { onApply(text) }
            ) {
                Text(text = stringResource(R.string.artist_releases_filter_apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.artist_releases_filter_cancel))
            }
        }
    )
}
