package com.bgflamer4ik.app.callblocker

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bgflamer4ik.app.callblocker.database.DataKeys
import com.bgflamer4ik.app.callblocker.service.NotificationKeys
import com.bgflamer4ik.app.callblocker.service.NotificationService
import kotlinx.coroutines.launch

@Composable
fun Settings(vm: ApplicationViewModel) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val windowInfo = LocalWindowInfo.current
        if (this.maxWidth > this.maxHeight) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Switches(
                    vm,
                    Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                )
                SpecialLinks(Modifier
                    .padding(8.dp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Switches(vm, Modifier.horizontalScroll(rememberScrollState()))
                SpecialLinks(Modifier
                    .padding(8.dp)
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState())
                    .requiredWidth(windowInfo.containerDpSize.width)
                )
            }
        }
    }
}

@Composable
private fun Switches(vm: ApplicationViewModel, modifier: Modifier) {
    val blockUndefined by vm.blockUndefined.collectAsState("false")
    val blockAll by vm.blockAll.collectAsState("false")
    val skipCallLog by vm.skipCallLog.collectAsState("false")
    val skipNotification by vm.skipNotification.collectAsState("false")

    Column(modifier = modifier) {
        SettingsBlock(
            stringResource(R.string.settings_param_block_undefined),
            blockUndefined == "true"
        ) {
            vm.update(DataKeys.DATA_BLOCK_UNDEFINED, it)
        }
        SettingsBlock(
            stringResource(R.string.settings_param_block_all_calls),
            blockAll == "true"
        ) {
            vm.update(DataKeys.DATA_BLOCK_ALL, it)
        }
        SettingsBlock(
            stringResource(R.string.settings_param_skip_call_log),
            skipCallLog == "true"
        ) {
            vm.update(DataKeys.DATA_SKIP_CALL_LOG, it)
        }
        SettingsBlock(
            stringResource(R.string.settings_param_skip_notification),
            skipNotification == "true"
        ) {
            vm.update(DataKeys.DATA_SKIP_NOTIFICATION, it)
        }
    }
}

@Composable
private fun SpecialLinks(modifier: Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it != null) {
            scope.launch {
                SettingsHelper.importNumberList(context, it)
            }
        }
    }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                onClick = {
                    notify(context,
                        context.getString(R.string.settings_notify_export_start),
                        NotificationKeys.PROGRESS)
                    scope.launch {
                        SettingsHelper.exportNumbersList(context, listOf(
                            DataKeys.BLACK_LIST_KEY,
                            DataKeys.WHITE_LIST_KEY
                        )).onSuccess {
                            notify(context, it)
                        }.onFailure {
                            notify(context,
                                context.getString(R.string.settings_notify_export_failed),
                                NotificationKeys.EMPTY_KEY)
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.settings_export))
            }
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                onClick = {
                    launcher.launch(arrayOf("application/json"))
                }
            ) {
                Text(stringResource(R.string.settings_import))
            }
        }
        Button(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            onClick = {
                RequestDialogHelper.showConfirmationDialog(
                    title = context.getString(R.string.settings_warning_external_links),
                    message = context.getString(R.string.settings_warning_external_link_text),
                    onConfirm = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://github.com/BGFlamer4ik/call_blocker/releases".toUri()
                        )
                        context.startActivity(intent)
                    },
                    onDiscard = {}
                )
            }
        ) {
            Text(stringResource(R.string.check_github_for_updates))
        }
    }
}

private fun notify(context: Context, text: String, key: String) {
    val intent = Intent(context, NotificationService::class.java)
        .putExtra("key", key)
        .putExtra("text", text)
    context.startForegroundService(intent)
}

private fun notify(context: Context, fileUri: Uri) {
    val intent = Intent(context, NotificationService::class.java)
        .putExtra("key", NotificationKeys.DATA_EXPORT)
        .putExtra("text", context.getString(R.string.settings_notify_export_success))
        .putExtra("fileUri", fileUri.toString())
    context.startForegroundService(intent)
}

@Composable
private fun SettingsBlock(
    text: String,
    param: Boolean,
    resultTo: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .border(
                2.dp,
                MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .requiredWidthIn(min = 120.dp)
                .padding(all = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .requiredWidth(320.dp),
                text = text,
                maxLines = 3,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Switch(
                checked = param,
                onCheckedChange = { resultTo(it) }
            )
        }
    }
}

@Composable
fun RequestPermission(context: Context) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("Role request", "Result OK")
        } else {
            Log.d("Role request", "Permission not granted")
        }
    }
    val launcherPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { result ->
        result.forEach { (_, isGranted) ->
            if (isGranted) {
                Log.d("Role request", "Result OK")
            } else {
                Log.d("Role request", "Permission not granted")
            }
        }
    }

    var isCSPNeed = false
    var isAddNeed = false

    val roleManager = context.getSystemService(RoleManager::class.java) as RoleManager
    if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) && !roleManager.isRoleHeld(
            RoleManager.ROLE_CALL_SCREENING
        )
    ) {
        isCSPNeed = true
    }
    var roles = arrayOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ANSWER_PHONE_CALLS,
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        roles += Manifest.permission.POST_NOTIFICATIONS
    }

    for (role in roles) {
        if (ContextCompat.checkSelfPermission(
                context,
                role
            ) == PackageManager.PERMISSION_DENIED
        ) {
            isAddNeed = true
            break
        }
    }

    if (isCSPNeed || isAddNeed) {
        RequestDialogHelper.showConfirmationDialog(
            title = stringResource(R.string.settings_req_dialog_perm_title),
            message = stringResource(R.string.settings_req_dialog_perm_text),
            onConfirm = {
                if (isCSPNeed) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                    launcher.launch(intent)
                }
                launcherPermissions.launch(roles)
            },
            onDiscard = {
                Toast.makeText(
                    context,
                    "Permissions required! App may take issues!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}