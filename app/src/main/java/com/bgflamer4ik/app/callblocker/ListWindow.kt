package com.bgflamer4ik.app.callblocker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgflamer4ik.app.callblocker.SettingsHelper.numberCorrector
import com.bgflamer4ik.app.callblocker.database.NumberData

@Composable
fun ListWindow(
    title: String,
    list: List<NumberData>,
    onEdit: (NumberData, NumberData) -> Unit,
    onDelete: (NumberData) -> Unit
) {
    val scroll = rememberScrollState()
    Box(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
    ) {
        AnimatedVisibility(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .horizontalScroll(scroll),
            visible = list.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                items(list) {
                    var isEdit by remember { mutableStateOf(false) }
                    var number by remember { mutableStateOf(it.number) }
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .requiredWidth(250.dp)
                                .defaultMinSize(minWidth = 100.dp),
                            value = number,
                            enabled = isEdit,
                            onValueChange = { num -> number = numberCorrector(num).number }
                        )
                        Spacer(Modifier.size(2.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .requiredWidth(100.dp),
                            horizontalArrangement = Arrangement.Absolute.Right
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .requiredSize(50.dp)
                                    .padding(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        RoundedCornerShape(16.dp)
                                    ),
                                onClick = {
                                    if (isEdit) {
                                        val tmp = numberCorrector(number)
                                        onEdit(it, tmp)
                                        number = tmp.number
                                    }
                                    isEdit = !isEdit
                                }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.secondary,
                                    imageVector = Icons.Default.Create,
                                    contentDescription =
                                        if (isEdit) stringResource(R.string.return_button_text)
                                        else stringResource(R.string.edit_button_text)
                                )
                            }
                            Spacer(Modifier.size(2.dp))
                            IconButton(
                                modifier = Modifier
                                    .requiredSize(50.dp)
                                    .padding(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        RoundedCornerShape(16.dp)
                                    ),
                                enabled = !isEdit,
                                onClick = { onDelete(it) }
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.secondary,
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete_button_text)
                                )
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            visible = list.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                text = "$title " + stringResource(R.string.is_empty),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}
