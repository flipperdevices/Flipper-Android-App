package com.flipperdevices.connection.impl.composable.status

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.connection.impl.R
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.connection.impl.viewmodel.ConnectionStatusViewModel

@Composable
fun ComposableConnectionStatus(
    connectionStatusViewModel: ConnectionStatusViewModel = viewModel()
) {
    val statusState by connectionStatusViewModel.getStatusState().collectAsState()

    ComposableConnectionBackground(statusState = statusState) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(size = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                ComposableSyncIcon(statusState = statusState)
            }

            ComposableTitle(
                modifier = Modifier.padding(horizontal = 20.dp),
                statusState = statusState
            )

            Box(
                modifier = Modifier.size(size = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                ComposableBluetoothIcon(statusState = statusState)
            }
        }
    }
}

@Composable
private fun ComposableSyncIcon(modifier: Modifier = Modifier, statusState: ConnectionStatusState) {
    when (statusState) {
        ConnectionStatusState.Disconnected,
        ConnectionStatusState.Connecting -> {
        } // Do nothing
        ConnectionStatusState.Synchronization -> Icon(
            modifier = modifier,
            painter = painterResource(id = R.drawable.ic_sync),
            tint = colorResource(id = R.color.state_border_synchronization_color),
            contentDescription = stringResource(
                id = R.string.connection_status_synchronization_pic_desc
            )
        )
        is ConnectionStatusState.Completed -> Icon(
            modifier = modifier,
            painter = painterResource(id = R.drawable.ic_check),
            tint = colorResource(id = R.color.state_border_connected_color),
            contentDescription = stringResource(
                id = R.string.connection_status_ok_pic_desc
            )
        )
    }
}

@Composable
private fun ComposableTitle(
    modifier: Modifier = Modifier,
    statusState: ConnectionStatusState
) {
    when (statusState) {
        ConnectionStatusState.Disconnected -> Text(
            modifier = modifier,
            text = stringResource(R.string.connection_status_bluetooth_disconnected_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        ConnectionStatusState.Connecting -> Text(
            modifier = modifier,
            text = stringResource(R.string.connection_status_bluetooth_connecting_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        ConnectionStatusState.Synchronization -> Text(
            modifier = modifier,
            text = stringResource(R.string.connection_status_synchronization_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        is ConnectionStatusState.Completed -> Text(
            modifier = modifier,
            text = statusState.deviceName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ComposableBluetoothIcon(
    modifier: Modifier = Modifier,
    statusState: ConnectionStatusState
) {
    when (statusState) {
        ConnectionStatusState.Disconnected -> Icon(
            modifier = modifier,
            painter = painterResource(id = R.drawable.ic_bluetooth_disabled),
            tint = colorResource(id = R.color.state_border_not_connected_color),
            contentDescription = stringResource(
                id = R.string.connection_status_bluetooth_disconnected_pic_desc
            )
        )
        ConnectionStatusState.Connecting -> Icon(
            modifier = modifier,
            painter = painterResource(id = R.drawable.ic_bluetooth),
            tint = colorResource(id = R.color.state_bluetooth_icon_active),
            contentDescription = stringResource(
                id = R.string.connection_status_bluetooth_connecting_pic_desc
            )
        )
        ConnectionStatusState.Synchronization,
        is ConnectionStatusState.Completed -> Icon(
            modifier = modifier,
            painter = painterResource(id = R.drawable.ic_bluetooth_connected),
            tint = colorResource(id = R.color.state_bluetooth_icon_active),
            contentDescription = stringResource(
                id = R.string.connection_status_bluetooth_pic_desc
            )
        )
    }
}
