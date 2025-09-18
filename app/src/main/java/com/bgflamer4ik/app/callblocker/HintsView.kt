package com.bgflamer4ik.app.callblocker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun HintView(onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(16.dp)
                )
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(R.string.help_1),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                HorizontalDivider(Modifier.padding(8.dp))
                Text(stringResource(R.string.help_2), fontSize = 18.sp)
                Text(stringResource(R.string.help_3), fontSize = 18.sp)
                Text(stringResource(R.string.help_4), fontSize = 18.sp)
                Text(stringResource(R.string.help_5), fontSize = 18.sp)
                HorizontalDivider(Modifier.padding(8.dp))
                Text(stringResource(R.string.help_6), fontSize = 18.sp)
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = { onDismissRequest }
                ) {
                    Text(stringResource(R.string.confirm_dialog_accept_button))
                }
            }
        }
    }
}