package com.bgflamer4ik.app.callblocker

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
fun ListsView(vm: ApplicationViewModel) {
    val context = LocalContext.current
    val notify = Toast.makeText(context, stringResource(R.string.pattern_edited), Toast.LENGTH_SHORT)

    val blacklist by vm.blacklist.collectAsState()
    val whitelist by vm.whitelist.collectAsState()

    var isWhiteSelected by remember { mutableStateOf(false) }
    var numberToAdd by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { isWhiteSelected = !isWhiteSelected }
            ) {
                Text(
                    text =
                        if (isWhiteSelected) stringResource(R.string.white_list_title)
                        else stringResource(R.string.black_list_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .defaultMinSize(minWidth = 100.dp),
            label = { Text(stringResource(R.string.number)) },
            value = numberToAdd,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Unspecified,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            onValueChange = {
                numberToAdd = it
            }
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            enabled = numberToAdd.isNotEmpty(),
            onClick = {
                val number = numberCorrector(numberToAdd)
                val listName = if (isWhiteSelected) DataKeys.WHITE_LIST_KEY else DataKeys.BLACK_LIST_KEY
                vm.add(number, listName)
                numberToAdd = ""
                }
            ) {
            Text(
                text = stringResource(R.string.add_to),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = !isWhiteSelected,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { -it }
        ) {
            ListWindow(
                title = stringResource(R.string.black_list_title),
                list = blacklist,
                onEdit = { old, new ->
                    vm.update(old, new, DataKeys.BLACK_LIST_KEY)
                    notify.show()
                    },
                onDelete = { vm.remove(it, DataKeys.BLACK_LIST_KEY) }
            )
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = isWhiteSelected,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { -it }
        ) {
            ListWindow(
                title = stringResource(R.string.white_list_title),
                list = whitelist,
                onEdit = { old, new ->
                        vm.update(old, new, DataKeys.WHITE_LIST_KEY)
                        notify.show()
                    },
                onDelete = { vm.remove(it, DataKeys.WHITE_LIST_KEY) }
            )
        }
    }
}