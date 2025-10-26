package com.bgflamer4ik.app.callblocker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgflamer4ik.app.callblocker.SettingsHelper.numberCorrector
import com.bgflamer4ik.app.callblocker.database.DataKeys

@Composable
fun HomeScreen(vm: ApplicationViewModel) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if (this.maxHeight > this.maxWidth) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                GreetingsBlock(vm)
                Spacer(Modifier.height(8.dp))
                HistoryWindow(vm)
            }
        } else {
            Row(
                modifier = Modifier
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GreetingsBlock(vm)
                //HistoryWindow(vm)
            }
        }
    }
}

@Composable
private fun GreetingsBlock(vm: ApplicationViewModel) {
    val context = LocalContext.current
    var fastAdd by remember { mutableStateOf("") }

    Column {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
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
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.home_screen_welcome),
                    maxLines = 2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                HorizontalDivider(Modifier.padding(4.dp))
                Text(
                    stringResource(R.string.home_screen_short_hint),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
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
                modifier = Modifier
                    .fillMaxWidth(0.75f),
                value = fastAdd,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Unspecified,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Unspecified,
                    imeAction = ImeAction.Done
                ),
                label = { Text(
                    stringResource(R.string.number_fast_add),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1) },
                onValueChange = { fastAdd = numberCorrector(it).number }
            )
            Spacer(Modifier.size(2.dp))
            IconButton(
                modifier = Modifier
                    .widthIn(min = 50.dp)
                    .fillMaxWidth(1f)
                    .border(
                        2.dp,
                        if (fastAdd.isNotEmpty()) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(16.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(16.dp)
                    ),
                enabled = fastAdd.isNotEmpty(),
                onClick = {
                    RequestDialogHelper.showConfirmationDialog(
                        title = context.getString(R.string.fast_add_number_to_blacklist_confirm_title),
                        message = context.getString(R.string.fast_add_number_to_blacklist_confirm_text) + fastAdd,
                        onConfirm = {
                            val number = numberCorrector(fastAdd)
                            vm.add(number, DataKeys.BLACK_LIST_KEY)
                            fastAdd = ""
                        },
                        onDiscard = {
                            fastAdd = ""
                        }
                    )
                }
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentDescription = stringResource(R.string.button_add_to_blacklist_desc)
                )
            }
        }
    }
}