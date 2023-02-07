
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.flipperdevices.core.ktx.android.observeAsState
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.firstpair.impl.R
import com.flipperdevices.firstpair.impl.composable.searching.ComposableSearchingScreen
import com.flipperdevices.firstpair.impl.model.SearchingContent
import com.flipperdevices.firstpair.impl.viewmodels.SearchStateBuilder
import com.flipperdevices.firstpair.impl.viewmodels.connecting.PairDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.PermissionChangeDetectBroadcastReceiver
import com.flipperdevices.firstpair.impl.viewmodels.searching.PermissionStateBuilder
import tangle.viewmodel.compose.tangleViewModel

@Composable
internal fun ComposableSearchingView(
    onHelpClicking: () -> Unit,
    onFinishConnection: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pairViewModel: PairDeviceViewModel = tangleViewModel()
    val bleDeviceViewModel: BLEDeviceViewModel = tangleViewModel()

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
        permissionStateBuilder
    ) {
        SearchStateBuilder(
            context = context,
            scope = scope,
            viewModelSearch = bleDeviceViewModel,
            viewModelConnecting = pairViewModel,
            permissionStateBuilder = permissionStateBuilder
        )
    }

    val broadcast = remember(searchStateBuilder) {
        PermissionChangeDetectBroadcastReceiver(searchStateBuilder)
    }

    ComposableSearchingInternal(
        bluetoothRequestState = bluetoothRequestState,
        permissionRequestState = permissionRequestState,
        broadcast = broadcast,
        processPermissionActivityResult = permissionStateBuilder::processPermissionActivityResult,
        processBluetoothActivityResult = permissionStateBuilder::processBluetoothActivityResult,
        invalidate = searchStateBuilder::invalidate
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
        onRefreshSearching = searchStateBuilder::resetByUser
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
    processBluetoothActivityResult: (ActivityResult) -> Unit,
    invalidate: () -> Unit
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

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.observeAsState()
    LaunchedEffect(key1 = lifecycleState) {
        when (lifecycleState) {
            Lifecycle.Event.ON_RESUME -> invalidate()
            else -> {}
        }
    }

    DisposableEffect(context) {
        broadcast.register(context)

        onDispose {
            broadcast.unregister(context)
        }
    }
}
