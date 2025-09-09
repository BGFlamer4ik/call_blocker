package com.bgflamer4ik.app.callblocker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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

@Composable
fun HistoryWindow(
    vm: ApplicationViewModel
) {
    val history by vm.history.collectAsState()
    Box(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .border(
                2.dp,
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                                modifier = Modifier.clickable(
                                    onClick = { isExpanded = !isExpanded }
                                ),
                                text = it.number
                            )
                            Button(
                                modifier = Modifier.size(50.dp),
                                onClick = { vm.remove(it) }
                            ) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
                        }
                        if (isExpanded) {
                            Column(
                                Modifier.padding(8.dp)
                                    .fillMaxWidth()
                            ) {

                                Text(
                                    if (it.block) stringResource(R.string.history_number_blocked)
                                    else stringResource(R.string.history_number_passed)
                                )
                                Text(DBHelper.patternDecrypt(it.pattern))
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