package com.bgflamer4ik.app.callblocker

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun DrawerMenu(
    navController: NavController,
    drawerState: DrawerState
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalDrawerSheet(
        modifier = Modifier.wrapContentWidth()
    ) {
        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
           Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .heightIn(max = 250.dp)
                    .padding(5.dp)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
           ) {
               Row(
                   horizontalArrangement = Arrangement.Center,
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(8.dp)
               ) {
                   context.getDrawable(R.drawable.logo)?.toBitmap()?.asImageBitmap()
                       ?.let {
                           Image(
                               it,
                               stringResource(R.string.app_name)
                           )
                       }
               }
           }
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
               icon = {
                   Icon(Icons.Default.AccountBox,
                       contentDescription = stringResource(R.string.lists_screen)
                   )
               },
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
           HorizontalDivider(Modifier.padding(6.dp))
           NavigationDrawerItem(
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