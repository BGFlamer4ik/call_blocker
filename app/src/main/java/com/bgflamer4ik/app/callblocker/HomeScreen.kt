package com.bgflamer4ik.app.callblocker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.bgflamer4ik.app.callblocker.database.DataKeys
import com.bgflamer4ik.app.callblocker.database.NumberData
import com.bgflamer4ik.app.callblocker.service.CallBlockerMatchHelper.patternToRegex

@Composable
fun HomeScreen(vm: ApplicationViewModel) {
    var fastAdd by remember { mutableStateOf("") }
    val history by vm.history.collectAsState()

    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(2.dp))
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.home_screen_welcome),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                HorizontalDivider(Modifier.padding(2.dp))
                Text(
                    stringResource(R.string.home_screen_short_hint),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = fastAdd,
                label = { Text(stringResource(R.string.number_fast_add))},
                onValueChange = { fastAdd = it }
            )
            Button(
                modifier = Modifier.size(50.dp),
                enabled = fastAdd.isNotEmpty(),
                onClick = {
                    val numberData = NumberData(
                        fastAdd,
                        fastAdd != patternToRegex(fastAdd).toString()
                    )
                    vm.add(numberData, DataKeys.blackListKey)
                }
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentDescription = stringResource(R.string.button_add_to_blacklist_desc)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        AnimatedVisibility(
            visible = history.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            HistoryWindow(vm)
        }
        AnimatedVisibility(
            visible = history.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                stringResource(R.string.home_screen_short_hint_history),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}