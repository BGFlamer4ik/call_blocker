package com.bgflamer4ik.app.callblocker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgflamer4ik.app.callblocker.SettingsHelper.numberCorrector
import com.bgflamer4ik.app.callblocker.database.DataKeys

@Composable
fun HomeScreen(vm: ApplicationViewModel) {
    val context = LocalContext.current
    var fastAdd by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.home_screen_welcome),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                HorizontalDivider(Modifier.padding(4.dp))
                Text(
                    stringResource(R.string.home_screen_short_hint),
                    maxLines = 2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .requiredWidthIn(min = 120.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .defaultMinSize(minWidth = 100.dp)
                    .requiredWidth(320.dp),
                value = fastAdd,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Unspecified,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Unspecified,
                    imeAction = ImeAction.Done
                ),
                label = { Text(stringResource(R.string.number_fast_add))},
                onValueChange = { fastAdd = numberCorrector(it).number }
            )
            IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .border(2.dp,
                        if (fastAdd.isNotEmpty()) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(16.dp))
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
                            vm.add(number, DataKeys.blackListKey)
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
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HistoryWindow(vm)
        }
    }
}