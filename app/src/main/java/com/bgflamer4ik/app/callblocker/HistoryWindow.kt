package com.bgflamer4ik.app.callblocker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.bgflamer4ik.app.callblocker.database.DBHelper
import com.bgflamer4ik.app.callblocker.database.DataKeys
import com.bgflamer4ik.app.callblocker.database.NumberData

@Composable
fun HistoryWindow(
    vm: ApplicationViewModel
) {
    val scroll = rememberScrollState()
    val history by vm.history.collectAsState()

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                2.dp,
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .horizontalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = history.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    items(history) {
                        var isExpanded by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier
                                    .clickable(
                                        onClick = { isExpanded = !isExpanded })
                                    .requiredWidth(320.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                text = it.number
                            )
                            IconButton(
                                modifier = Modifier
                                    .requiredSize(50.dp)
                                    .padding(8.dp)
                                    .border(2.dp,
                                        MaterialTheme.colorScheme.secondary,
                                        RoundedCornerShape(16.dp))
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        RoundedCornerShape(16.dp)
                                    ),
                                onClick = { vm.remove(it) }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    modifier = Modifier.fillMaxSize(),
                                    contentDescription = stringResource(R.string.delete_button_text)
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Column(
                                Modifier
                                    .padding(8.dp)
                                    .fillParentMaxWidth()
                            ) {
                                Text(
                                    if (it.block) stringResource(R.string.history_number_blocked)
                                    else stringResource(R.string.history_number_passed),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                )
                                Text(
                                    DBHelper.patternDecrypt(it.params),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(0.5f),
                                        onClick = {
                                            vm.add(
                                                NumberData(
                                                    number = it.number,
                                                    hasPattern = false
                                                ),
                                                listName = DataKeys.BLACK_LIST_KEY
                                            )
                                        }
                                    ) {
                                        Text("Block")
                                    }
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            vm.add(
                                                NumberData(
                                                    number = it.number,
                                                    hasPattern = false
                                                ),
                                                listName = DataKeys.WHITE_LIST_KEY
                                            )
                                        }
                                    ) {
                                        Text(" or not?..")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = history.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = stringResource(R.string.history_is_empty),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    }
}