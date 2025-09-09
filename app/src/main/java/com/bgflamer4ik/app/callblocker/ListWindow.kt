package com.bgflamer4ik.app.callblocker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgflamer4ik.app.callblocker.database.NumberData
import com.bgflamer4ik.app.callblocker.service.CallBlockerMatchHelper

@Composable
fun ListWindow(
    title: String,
    list: List<NumberData>,
    onEdit: (NumberData, NumberData) -> Unit,
    onDelete: (NumberData) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
    ) {
        if (list.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                items(list) {
                    var isEdit by remember { mutableStateOf(false) }
                    var number = it.number
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .width(200.dp),
                            value = number,
                            enabled = isEdit,
                            onValueChange = { num -> number = num }
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                modifier = Modifier.size(50.dp),
                                onClick = {
                                    if (isEdit) {
                                        onEdit(
                                            it,
                                            NumberData(
                                                number,
                                                number != CallBlockerMatchHelper.patternToRegex(
                                                    number
                                                ).toString()
                                            )
                                        )
                                    }
                                    isEdit = !isEdit
                                }
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    imageVector = Icons.Default.Create,
                                    contentDescription =
                                        if (isEdit) stringResource(R.string.return_button_text)
                                        else stringResource(R.string.edit_button_text)
                                )
                            }
                            Button(
                                modifier = Modifier.size(50.dp),
                                enabled = !isEdit,
                                onClick = { onDelete(it) }
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete_button_text)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "$title " + stringResource(R.string.is_empty),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}
