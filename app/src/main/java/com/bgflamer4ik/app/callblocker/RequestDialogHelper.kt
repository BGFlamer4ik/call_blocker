package com.bgflamer4ik.app.callblocker

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDiscard: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDiscard,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) { Text(stringResource(R.string.confirm_dialog_accept_button)) }
        },
        dismissButton = {
            Button(onClick = onDiscard) {Text(stringResource(R.string.confirm_dialog_decline_button))}
        }
    )
}

object RequestDialogHelper {
    private var showDialog by mutableStateOf(false)
    private var dialogConfig: ConfirmDialogConfig? by mutableStateOf(null)

    data class ConfirmDialogConfig(
        val title: String,
        val message: String,
        val onConfirm: () -> Unit,
        val onDiscard: () -> Unit
    )

    fun showConfirmationDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onDiscard: () -> Unit
    ) {
        dialogConfig = ConfirmDialogConfig(
            title = title,
            message = message,
            onConfirm = onConfirm,
            onDiscard = onDiscard
        )
        showDialog = true
    }

    @Composable
    fun RenderDialogs() {
        val config = dialogConfig

        if (showDialog && config != null) {
            ConfirmDialog(
                title = config.title,
                message = config.message,
                onConfirm = {
                    config.onConfirm()
                    showDialog = false
                },
                onDiscard = {
                    config.onDiscard()
                    showDialog = false
                }
            )
        }
    }
}