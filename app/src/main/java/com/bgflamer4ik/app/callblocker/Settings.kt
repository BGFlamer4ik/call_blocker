package com.bgflamer4ik.app.callblocker

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.bgflamer4ik.app.callblocker.database.DataKeys


@Composable
fun Settings(vm: ApplicationViewModel) {
    val scrollState = rememberScrollState()

    val blockUndefined by vm.blockUndefined.collectAsState("false")
    val blockAll by vm.blockAll.collectAsState("false")
    val skipCallLog by vm.skipCallLog.collectAsState("false")
    val skipNotification by vm.skipNotification.collectAsState("false")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        SettingsBlock(
            stringResource(R.string.settings_param_block_undefined),
            blockUndefined == "true"
        ) {
            vm.update(DataKeys.dataBlockUndefined, it)
        }
        SettingsBlock(
            stringResource(R.string.settings_param_block_all_calls),
            blockAll == "true"
        ) {
            vm.update(DataKeys.dataBlockAll, it)
        }
        SettingsBlock(
            stringResource(R.string.settings_param_skip_call_log),
            skipCallLog == "true"
        ) {
            vm.update(DataKeys.dataSkipCallLog, it)
        }
        SettingsBlock(
            stringResource(R.string.settings_param_skip_notification),
            skipNotification == "true"
        ) {
            vm.update(DataKeys.dataSkipNotification, it)
        }
    }
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
                .padding(all = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
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