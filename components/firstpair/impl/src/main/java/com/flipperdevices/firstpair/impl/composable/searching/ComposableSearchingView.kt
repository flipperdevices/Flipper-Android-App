import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.dialog.composable.multichoice.addButton
import com.flipperdevices.core.ui.dialog.composable.multichoice.setDescription
import com.flipperdevices.core.ui.dialog.composable.multichoice.setTitle
import com.flipperdevices.firstpair.impl.R
import com.flipperdevices.firstpair.impl.composable.searching.ComposableSearchingScreen
import com.flipperdevices.firstpair.impl.model.SearchingContent
import com.flipperdevices.firstpair.impl.viewmodels.SearchStateBuilder
import com.flipperdevices.firstpair.impl.viewmodels.connecting.PairDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.PermissionChangeDetectBroadcastReceiver
import com.flipperdevices.firstpair.impl.viewmodels.searching.PermissionStateBuilder

@Composable
internal fun ComposableSearchingView(
    pairViewModel: PairDeviceViewModel,
    bleDeviceViewModel: BLEDeviceViewModel,
    lifecycleOwner: LifecycleOwner,
    onHelpClicking: () -> Unit,
    onFinishConnection: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val permissionStateBuilder = remember(context) { PermissionStateBuilder(context = context) }

    val permissionRequestState by permissionStateBuilder.permissionEnableState().collectAsState()
    val bluetoothRequestState by permissionStateBuilder.bluetoothEnableState().collectAsState()
    val locationRequestState by permissionStateBuilder.locationEnableState().collectAsState()

    if (locationRequestState) {
        ComposableLocationEnableDialog(
            onCancel = permissionStateBuilder::processLocationCancel,
            onAccept = permissionStateBuilder::processLocationSettings
        )
    }

    val searchStateBuilder = remember(
        context,
        scope,
        bleDeviceViewModel,
        pairViewModel,
        permissionStateBuilder,
        lifecycleOwner
    ) {
        SearchStateBuilder(
            context = context,
            scope = scope,
            viewModelSearch = bleDeviceViewModel,
            viewModelConnecting = pairViewModel,
            permissionStateBuilder = permissionStateBuilder
        ).also { lifecycleOwner.lifecycle.subscribe(it) }
    }

    val broadcast = remember(searchStateBuilder) {
        PermissionChangeDetectBroadcastReceiver(searchStateBuilder)
    }

    ComposableSearchingInternal(
        bluetoothRequestState = bluetoothRequestState,
        permissionRequestState = permissionRequestState,
        broadcast = broadcast,
        processPermissionActivityResult = permissionStateBuilder::processPermissionActivityResult,
        processBluetoothActivityResult = permissionStateBuilder::processBluetoothActivityResult
    )

    val state by searchStateBuilder.getState().collectAsState()
    (state.content as? SearchingContent.Finished)?.let {
        pairViewModel.finishConnection(it.deviceId, it.deviceName, onFinishConnection)
    }

    ComposableSearchingScreen(
        state = state,
        onBack = onBack,
        onHelpClicking = onHelpClicking,
        onSkipConnection = { pairViewModel.finishConnection(onEndAction = onFinishConnection) },
        onDeviceClick = pairViewModel::startConnectToDevice,
        onRefreshSearching = searchStateBuilder::resetByUser,
        onResetTimeoutState = pairViewModel::resetConnection
    )
}

@Composable
private fun ComposableLocationEnableDialog(
    onCancel: () -> Unit,
    onAccept: () -> Unit
) {
    val dialogModel = remember(onCancel, onAccept) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.firstpair_permission_enable_location_title)
            .setDescription(R.string.firstpair_permission_location_dialog)
            .setOnDismissRequest(onCancel)
            .addButton(
                R.string.firstpair_permission_settings,
                onAccept,
                isActive = true
            )
            .addButton(R.string.firstpair_permission_cancel_btn, onCancel)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}

@Composable
private fun ComposableSearchingInternal(
    bluetoothRequestState: Boolean,
    permissionRequestState: Array<String>,
    broadcast: PermissionChangeDetectBroadcastReceiver,
    processPermissionActivityResult: (Map<String, Boolean>) -> Unit,
    processBluetoothActivityResult: (ActivityResult) -> Unit
) {
    val context = LocalContext.current

    val permissionActivityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = processPermissionActivityResult
    )

    LaunchedEffect(key1 = permissionRequestState) {
        if (permissionRequestState.isNotEmpty()) {
            permissionActivityResult.launch(permissionRequestState)
        }
    }

    val bluetoothActivityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = processBluetoothActivityResult
    )

    LaunchedEffect(key1 = bluetoothRequestState) {
        if (bluetoothRequestState) {
            bluetoothActivityResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    DisposableEffect(context) {
        broadcast.register(context)

        onDispose {
            broadcast.unregister(context)
        }
    }
}
