package com.bgflamer4ik.app.callblocker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun DrawerMenu(
    navController: NavController,
    drawerState: DrawerState
) {
    val coroutineScope = rememberCoroutineScope()

    ModalDrawerSheet {
        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
           /* Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(1.dp)
                    .background(color = Color.Transparent)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()) {
                    ContextCompat.getDrawable(context, R.drawable.logo)?.toBitmap()?.asImageBitmap()
                        ?.let { Image(it, "") }
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                }
            } */
            NavigationDrawerItem(
                label = { Text(
                    stringResource(R.string.home_screen),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ) },
                icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.home_screen))},
                selected = false,
                onClick = {
                    navController.navigate("home")
                    coroutineScope.launch { drawerState.close() }
                }
            )
            NavigationDrawerItem(
                label = {
                    Text(
                        stringResource(R.string.lists_screen),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                icon = { Icon(Icons.Default.AccountBox, contentDescription = stringResource(R.string.lists_screen))},
                selected = false,
                onClick = {
                    navController.navigate("lists")
                    coroutineScope.launch { drawerState.close() }
                }
            )
            NavigationDrawerItem(
                label = {
                    Text(
                        stringResource(R.string.history),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                icon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.history)
                    )
                },
                selected = false,
                onClick = {
                    navController.navigate("history")
                    coroutineScope.launch { drawerState.close() }
                }
            )
            Spacer(Modifier
                .fillMaxHeight()
                .weight(0.7f))
            HorizontalDivider(Modifier.padding(6.dp))
            NavigationDrawerItem(
                modifier = Modifier.weight(0.1f),
                icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_screen)) },
                label = {
                    Text(stringResource(R.string.settings_screen),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) },
                selected = false,
                onClick = {
                    navController.navigate("settings")
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    }
}