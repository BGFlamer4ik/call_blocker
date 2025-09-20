package com.bgflamer4ik.app.callblocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bgflamer4ik.app.callblocker.RequestDialogHelper.RenderDialogs
import com.bgflamer4ik.app.callblocker.database.DBHelper
import com.bgflamer4ik.app.callblocker.database.DBRepository
import com.bgflamer4ik.app.callblocker.database.DataKeys
import com.bgflamer4ik.app.callblocker.database.KeyData
import com.bgflamer4ik.app.callblocker.service.NotificationService
import com.bgflamer4ik.app.callblocker.ui.theme.MainTheme
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.launch

class Main : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DBHelper.updateKeys(this)
        val isFirstLaunch = DBRepository(this).getKeySync(DataKeys.FIRST_LAUNCH_KEY)

        enableEdgeToEdge()
        setContent {
            MainTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (isFirstLaunch != "false") {
                        EulaConfirmationDialog(
                            onConfirm = {
                                val intent = Intent(this, NotificationService::class.java)
                                startForegroundService(intent)
                                DBRepository(this).update(KeyData(DataKeys.FIRST_LAUNCH_KEY, "false"))
                            },
                            onDiscard = {
                                this.finishAffinity()
                            }
                        )
                    }
                    RequestPermission(LocalContext.current)
                    RenderDialogs()
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val vm: ApplicationViewModel = viewModel()
    val navController = rememberNavController()

    var showHint by remember { mutableStateOf(false) }

    if (showHint) {
        HintView { showHint = !showHint }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(
                navController,
                drawerState
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name) )},
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            }
                        ) { Icon(
                            Icons.Default.Menu,
                            contentDescription = stringResource(R.string.menu))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .sizeIn(
                            maxWidth = 50.dp,
                            maxHeight = 50.dp
                        ),
                    onClick = { showHint = !showHint }
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = stringResource(R.string.hint_dialog_button),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    )
                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(it)
            ) {
                composable("home") { HomeScreen(vm) }
                composable("history") { HistoryWindow(vm) }
                composable("lists") { ListsView(vm) }
                composable("settings") { Settings(vm) }
            }
        }
    }
}

@Composable
private fun EulaConfirmationDialog(
    onConfirm: () -> Unit,
    onDiscard: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    AlertDialog(
        onDismissRequest = onDiscard,
        title = { Text(stringResource(R.string.eula)) },
        text = {
            Column {
                Text(stringResource(R.string.first_launch_confirmation_eula_message))
                Text(
                    text = "GitHub...",
                    style = TextStyle(color = Color.Blue),
                    modifier = Modifier.clickable(
                        onClick = {
                            val dotenv = dotenv {
                                directory = "/assets"
                                filename = "env"
                            }
                            uriHandler.openUri(dotenv.get("GIT_LINK"))
                        }
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text(stringResource(R.string.confirm_dialog_accept_button)) }
        },
        dismissButton = {
            Button(onClick = onDiscard) {Text(stringResource(R.string.confirm_dialog_decline_button))}
        }
    )
}